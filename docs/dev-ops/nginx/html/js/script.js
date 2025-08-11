// 全局变量
let isLoggedIn = false;
let currentUsername = ''; // 当前登录用户名
let modalContainer = null;

document.addEventListener('DOMContentLoaded', function() {
    // 创建模态框容器
    modalContainer = document.createElement('div');
    modalContainer.className = 'modal-container';
    document.body.appendChild(modalContainer);
    
    // API接口地址
    const API_BASE_URL = 'http://127.0.0.1:8091';

    // 商品和活动数据
    let goodsData = null;
    let activityData = null;
    let teamListData = null;
    let teamStatisticData = null;
    let currentTeamId = ''; // 当前选择的拼团ID
    let currentOrderNo = ''; // 当前订单号

    // 生成随机订单号
    function generateOrderNo() {
        return Math.floor(Math.random() * 900000000000) + 100000000000;
    }

    // 锁单接口
    async function lockOrder(teamId) {
        try {
            // 检查登录状态
            if (!isLoggedIn) {
                createLoginModal();
                return;
            }

            // 生成随机订单号
            currentOrderNo = generateOrderNo();

            const response = await fetch(`${API_BASE_URL}/api/v1/gbp/trade/lock-pay-order`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': '*/*',
                    'Accept-Encoding': 'gzip, deflate, br',
                    'Connection': 'keep-alive',
                    'User-Agent': 'PostmanRuntime-ApipostRuntime/1.1.0'
                },
                body: JSON.stringify({
                    userId: currentUsername || '', // 未登录时传递空字符串
                    teamId: teamId || '',
                    activityId: activityData.activityId,
                    goodsId: goodsData.goodsId,
                    source: 's01',
                    channel: 'c01',
                    outTradeNo: currentOrderNo,
                    notifyUrl: 'http://127.0.0.1:8091/api/v1/test/group_buy_notify'
                })
            });

            const result = await response.json();

            if (result.code === '0000') {
                console.log('锁单成功，订单号:', currentOrderNo);
                createPaymentModal();
            } else {
                alert('锁单失败: ' + result.info);
            }
        } catch (error) {
            console.error('锁单接口调用异常:', error);
            alert('系统异常，请稍后再试');
        }
    }

    // 结算接口
    async function settleOrder() {
        if (!currentOrderNo) {
            alert('订单号不存在，请重新下单');
            return;
        }

        // 获取选中的支付方式
        const selectedPayment = document.querySelector('.payment-method.active');
        const payType = selectedPayment ? selectedPayment.getAttribute('data-pay-type') : 'WECHAT';

        try {
            const response = await fetch(`${API_BASE_URL}/api/v1/gbp/trade/settlement_market_pay_order`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': '*/*',
                    'Accept-Encoding': 'gzip, deflate, br',
                    'Connection': 'keep-alive',
                    'User-Agent': 'PostmanRuntime-ApipostRuntime/1.1.0'
                },
                body: JSON.stringify({
                    source: 's01',
                    channel: 'c01',
                    userId: currentUsername || '',
                    outTradeNo: currentOrderNo,
                    outTradeTime: new Date().toISOString()
                })
            });

            const result = await response.json();

            if (result.code === '0000') {
                alert('支付成功！');
                modalContainer.style.display = 'none';
                // 刷新拼团列表
                loadGroupBuyDetail();
            } else {
                alert('支付失败: ' + result.info);
            }
        } catch (error) {
            console.error('结算接口调用异常:', error);
            alert('支付异常，请稍后再试');
        }
    }

    // 轮播图功能
    const carouselWrapper = document.querySelector('.carousel-wrapper');
    const slides = document.querySelectorAll('.carousel-slide');
    const indicators = document.querySelectorAll('.indicator');
    let currentIndex = 0;
    let startX, moveX;
    let autoPlayInterval;

    // 设置轮播图初始位置
    updateCarousel();

    // 自动轮播
    startAutoPlay();

    // 加载拼团详情数据
    loadGroupBuyDetail();

    // 拼团详情查询API
    async function loadGroupBuyDetail() {
        try {
            const response = await fetch(`${API_BASE_URL}/api/v1/gbm/index/query_group_buy_market_config`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': '*/*',
                    'Accept-Encoding': 'gzip, deflate, br',
                    'Connection': 'keep-alive',
                    'User-Agent': 'PostmanRuntime-ApipostRuntime/1.1.0'
                },
                body: JSON.stringify({
                    userId: currentUsername || '', // 未登录时传递空字符串
                    source: 's01',
                    channel: 'c01',
                    goodsId: '9890001'
                })
            });

            const result = await response.json();

            if (result.code === '0000') {
                // 保存数据
                goodsData = result.data.goods;
                activityData = result.data.activity;
                teamListData = result.data.teamList;
                teamStatisticData = result.data.teamStatistic;

                // 更新页面显示
                updatePageWithData();
            } else {
                console.error('获取拼团详情失败:', result.info);
            }
        } catch (error) {
            console.error('API调用异常:', error);
        }
    }

    // 更新页面显示
    function updatePageWithData() {
        if (!goodsData || !activityData) return;

        // 更新商品信息
        document.querySelector('.product-title').textContent = goodsData.goodsName;
        document.querySelector('.current-price').textContent = `¥${goodsData.payPrice.toFixed(2)}`;
        document.querySelector('.original-price').textContent = `¥${goodsData.originalPrice.toFixed(2)}`;

        // 更新活动信息
        const salesInfoSpan = document.querySelector('.sales-info span:first-child');
        salesInfoSpan.textContent = `已拼${teamStatisticData.allTeamUserCount || 0}件 | ${activityData.activityName}`;

        // 更新倒计时 - 使用活动结束时间
        const endTime = new Date(activityData.endTime);
        updateActivityCountdown(endTime);

        // 更新拼团列表
        updateTeamList();

        // 更新底部按钮价格
        document.querySelector('.buy-alone-btn').textContent = `¥${goodsData.originalPrice.toFixed(2)} 单独购买`;
        document.querySelector('.start-group-btn').textContent = `¥${goodsData.payPrice.toFixed(2)} ${activityData.discountName}`;
    }

    // 更新拼团列表
    function updateTeamList() {
        const groupInfoContainer = document.querySelector('.group-info');

        // 清空现有内容，保留标题
        const sectionTitle = groupInfoContainer.querySelector('.section-title');
        groupInfoContainer.innerHTML = '';
        groupInfoContainer.appendChild(sectionTitle);

        // 如果没有拼团数据，显示提示信息
        if (!teamListData || teamListData.length === 0) {
            const emptyTip1 = document.createElement('div');
            emptyTip1.className = 'empty-team-tip';
            emptyTip1.textContent = '当前还没有拼团哦~';
            groupInfoContainer.appendChild(emptyTip1);
            const emptyTip2 = document.createElement('div');
            emptyTip2.className = 'empty-team-tip';
            emptyTip2.textContent = '小伙伴，心动不如行动，赶紧去开团吧！';
            groupInfoContainer.appendChild(emptyTip2);
            return;
        }

        // 添加拼团列表
        teamListData.forEach(team => {
            const groupMember = document.createElement('div');
            groupMember.className = 'group-member';

            const remainCount = team.targetCount - team.lockCount;
            const validEndTime = new Date(team.validEndTime);

            groupMember.innerHTML = `
                <div class="avatar">${team.userId.substring(0, 1)}</div>
                <div class="member-info">
                    <div class="member-name">${team.userId}（团友）</div>
                    <div class="group-status">还差${remainCount}人，<span class="team-countdown" data-end="${team.validEndTime}">${team.validTimeCountdown}</span></div>
                </div>
                <button class="join-btn" data-team-id="${team.teamId}">去拼团</button>
            `;

            groupInfoContainer.appendChild(groupMember);
        });

        // 为新添加的拼团按钮绑定事件
        document.querySelectorAll('.join-btn').forEach(btn => {
            btn.addEventListener('touchstart', () => {
                btn.style.opacity = '0.8';
            });
            btn.addEventListener('touchend', () => {
                btn.style.opacity = '1';
                currentTeamId = btn.getAttribute('data-team-id');
                // 设置购买类型标志
                window.buyType = 'join';

                // 检查登录状态
                if (isLoggedIn) {
                    // 已登录，调用锁单接口
                    lockOrder(currentTeamId);
                } else {
                    // 未登录，显示登录页面
                    createLoginModal();
                }
            });
        });

        // 更新每个团的倒计时
        updateTeamCountdowns();
    }

    // 全局定时器变量
    let activityCountdownTimer = null;

    // 更新活动倒计时
    function updateActivityCountdown(endTime) {
        const timerElement = document.getElementById('timer');

        // 清除之前的定时器
        if (activityCountdownTimer) {
            clearInterval(activityCountdownTimer);
        }

        function updateTimer() {
            const now = new Date();
            const diff = endTime - now;

            if (diff <= 0) {
                timerElement.textContent = '00:00:00';
                return;
            }

            // 如果剩余时间超过一天，则以天为单位显示
            const days = Math.floor(diff / (1000 * 60 * 60 * 24));
            const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
            const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
            const seconds = Math.floor((diff % (1000 * 60)) / 1000);

            if (days > 0) {
                timerElement.textContent = `${days}天${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
            } else {
                timerElement.textContent = `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
            }
        }

        // 立即更新一次
        updateTimer();

        // 每秒更新一次
        activityCountdownTimer = setInterval(updateTimer, 1000);
    }

    // 团队倒计时定时器数组
    let teamCountdownTimers = [];

    // 更新团倒计时
    function updateTeamCountdowns() {
        // 清除之前的所有定时器
        teamCountdownTimers.forEach(timer => clearInterval(timer));
        teamCountdownTimers = [];

        const countdownElements = document.querySelectorAll('.team-countdown');

        countdownElements.forEach(element => {
            const endTimeStr = element.getAttribute('data-end');
            const endTime = new Date(endTimeStr);

            function updateTimer() {
                const now = new Date();
                const diff = endTime - now;

                if (diff <= 0) {
                    element.textContent = '拼团已结束';
                    return;
                }

                // 如果剩余时间超过一天，则以天为单位显示
                const days = Math.floor(diff / (1000 * 60 * 60 * 24));
                const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
                const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
                const seconds = Math.floor((diff % (1000 * 60)) / 1000);

                if (days > 0) {
                    element.textContent = `拼团剩余时间：${days}天${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
                } else {
                    element.textContent = `拼团剩余时间：${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
                }
            }

            // 立即更新一次
            updateTimer();

            // 每秒更新一次，并保存定时器ID
            const timerId = setInterval(updateTimer, 1000);
            teamCountdownTimers.push(timerId);
        });
    }

    // 指示器点击事件
    indicators.forEach((indicator, index) => {
        indicator.addEventListener('click', () => {
            currentIndex = index;
            updateCarousel();
            resetAutoPlay();
        });
    });

    // 触摸事件 - 开始
    carouselWrapper.addEventListener('touchstart', (e) => {
        startX = e.touches[0].clientX;
        clearInterval(autoPlayInterval);
    }, { passive: true });

    // 触摸事件 - 移动
    carouselWrapper.addEventListener('touchmove', (e) => {
        if (!startX) return;
        moveX = e.touches[0].clientX;
        const diff = moveX - startX;
        carouselWrapper.style.transform = `translateX(calc(-${currentIndex * 33.333}% + ${diff}px))`;
    }, { passive: true });

    // 触摸事件 - 结束
    carouselWrapper.addEventListener('touchend', () => {
        if (!startX || !moveX) return;
        const diff = moveX - startX;
        if (diff > 50 && currentIndex > 0) {
            currentIndex--;
        } else if (diff < -50 && currentIndex < slides.length - 1) {
            currentIndex++;
        }
        updateCarousel();
        startAutoPlay();
        startX = null;
        moveX = null;
    });

    function updateCarousel() {
        carouselWrapper.style.transform = `translateX(-${currentIndex * 33.333}%)`;
        indicators.forEach((indicator, index) => {
            indicator.classList.toggle('active', index === currentIndex);
        });
    }

    function startAutoPlay() {
        autoPlayInterval = setInterval(() => {
            currentIndex = (currentIndex + 1) % slides.length;
            updateCarousel();
        }, 3000);
    }

    function resetAutoPlay() {
        clearInterval(autoPlayInterval);
        startAutoPlay();
    }

    // 注意：旧的倒计时功能已被移除，现在使用API数据驱动的倒计时

    // 检查登录状态
    function checkLoginStatus() {
        // 如果未登录，显示登录页面
        if (!isLoggedIn) {
            createLoginModal();
        }
    }

    // 创建登录页面
    function createLoginModal() {
        modalContainer.innerHTML = `
            <div class="login-modal">
                <div class="modal-header">
                    <div class="back-btn modal-close">
                        <svg t="1754881488453" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="1480" width="150" height="150">
                            <path d="M624.788992 204.047974 585.205965 164.464026 219.560038 530.185011 585.205965 895.864013 624.788992 856.280986 298.663014 530.16105Z" p-id="1481">
                            </path>
                        </svg>
                    </div>
                    <div class="title">登录</div>
                    <div style="width: 24px;"></div>
                </div>
                
                <div class="logo-area">
                    <div class="logo-circle">
                        <span>拼团Plus</span>
                    </div>
                </div>
                
                <div class="login-form">
                    <div class="input-group">
                        <input type="text" placeholder="用户名" id="username">
                    </div>
                    <div class="input-group">
                        <input type="password" placeholder="密码" id="password">
                        <span class="forget-pwd">忘记密码?</span>
                    </div>
                    <div class="debug-info">
                        <p>当前为调试模式: 任意填写用户名和密码即可登录</p>
                    </div>
                    <button class="login-btn">登录</button>
                </div>
                
                <div class="login-footer">
                    <p class="agreement">登录即表示同意<span class="highlight">《用户协议》和《隐私政策》</span></p>
                    <p class="register">还没有账号？<span class="highlight">立即注册</span></p>
                </div>
            </div>
        `;

        // 显示模态框
        modalContainer.style.display = 'flex';

        // 关闭按钮事件
        document.querySelector('.modal-close').addEventListener('click', () => {
            modalContainer.style.display = 'none';
        });

        // 登录按钮事件
        document.querySelector('.login-btn').addEventListener('click', () => {
            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;

            // 校验用户名（模拟Java的StringUtils.isBlank()）
            const isUsernameBlank = !username || username.trim() === '' || /^\s*$/.test(username);

            if (isUsernameBlank) {
                alert('用户名不能为空或只包含空白字符');
                return;
            }

            if (!password || password.trim() === '') {
                alert('密码不能为空');
                return;
            }

            // 模拟登录成功
            isLoggedIn = true;
            currentUsername = username.trim(); // 保存用户名（去除首尾空格）
            modalContainer.style.display = 'none';

            // 登录成功后，重新调用拼团详情页接口刷新页面
            loadGroupBuyDetail().then(() => {
                // 页面刷新完成后，如果有待处理的操作，继续执行
                if (currentTeamId) {
                    lockOrder(currentTeamId);
                } else if (window.buyType) {
                    lockOrder();
                }
            });
        });
    }

    // 创建支付页面
    function createPaymentModal() {
        // 如果没有商品数据，无法创建支付页面
        if (!goodsData || !activityData) {
            alert('商品信息加载失败，请刷新页面重试');
            return;
        }

        // 确定显示价格（根据购买类型和团ID决定是单独购买还是拼团价格）
        let displayPrice = goodsData.originalPrice; // 默认使用原价

        // 如果是从拼团按钮进入或有团ID，使用拼团价格
        if (currentTeamId || window.buyType === 'group' || window.buyType === 'join') {
            displayPrice = goodsData.payPrice;
        }

        // 设置支付倒计时（15分钟）
        const paymentDeadline = new Date().getTime() + 15 * 60 * 1000;

        modalContainer.innerHTML = `
            <div class="payment-modal">
                <div class="modal-header">
                    <div class="back-btn modal-close">
                        <svg t="1754881488453" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="1480" width="150" height="150">
                            <path d="M624.788992 204.047974 585.205965 164.464026 219.560038 530.185011 585.205965 895.864013 624.788992 856.280986 298.663014 530.16105Z" p-id="1481">
                            </path>
                        </svg>
                    </div>
                    <div class="title">确认支付</div>
                    <div style="width: 24px;"></div>
                </div>
                
                <div class="payment-content">
                    <div class="payment-section">
                        <h3>订单信息</h3>
                        <p>订单编号：${currentOrderNo}</p>
                        <p>下单时间：${new Date().toLocaleString()}</p>
                    </div>
                    
                    <div class="payment-section product-info-payment">
                        <div class="product-image">
                            <img src="http://codezj.top/wp-content/uploads/2025/08/01.png" height="80px" width="80px" alt="商品图片">
                        </div>
                        <div class="product-details">
                            <h4>${goodsData.goodsName}</h4>
                            <p>数量：1件</p>
                        </div>
                        <div class="product-price">¥${displayPrice.toFixed(2)}</div>
                    </div>
                    
                    <div class="payment-section">
                        <h3>拼团信息</h3>
                        <div class="group-info-payment">
                            <span>${currentTeamId ? '参与拼团' : '开启拼团'} | ${activityData.activityName}</span>
                            <span class="highlight">支付倒计时：<span id="payment-timer">${activityData.validTime}:00</span></span>
                        </div>
                    </div>
                    
                    <div class="payment-section">
                        <h3>收货地址</h3>
                        <div class="address-info">
                            <p><strong>${currentUsername} 19900001111</strong></p>
                            <p>北京市海淀区中关村南大街5号北京理工大学快递驿站</p>
                        </div>
                    </div>
                    
                    <div class="payment-section">
                        <h3>支付方式</h3>
                        <div class="payment-methods">
                            <div class="payment-method active" data-pay-type="WECHAT">
                                <div class="payment-icon wechat"></div>
                                <span>微信支付</span>
                                <div class="radio-btn checked"></div>
                            </div>
                            <div class="payment-method" data-pay-type="ALIPAY">
                                <div class="payment-icon alipay"></div>
                                <span>支付宝</span>
                                <div class="radio-btn"></div>
                            </div>
                            <div class="payment-method" data-pay-type="BANK">
                                <div class="payment-icon bank"></div>
                                <span>银行卡支付</span>
                                <div class="radio-btn"></div>
                            </div>
                        </div>
                    </div>
                </div>
                
                <div class="payment-footer">
                    <div class="total-amount">
                        <span>实付款：</span>
                        <span class="highlight">¥${displayPrice.toFixed(2)}</span>
                    </div>
                    <button class="confirm-payment-btn">确认支付</button>
                </div>
            </div>
        `;

        // 设置支付倒计时
        let paymentMinutes = 15;
        let paymentSeconds = 0;
        const paymentTimerElement = document.getElementById('payment-timer');
        const paymentCountdownInterval = setInterval(() => {
            if (paymentSeconds === 0) {
                if (paymentMinutes === 0) {
                    clearInterval(paymentCountdownInterval);
                    alert('支付超时，请重新下单');
                    modalContainer.style.display = 'none';
                    return;
                }
                paymentMinutes--;
                paymentSeconds = 59;
            } else {
                paymentSeconds--;
            }
            paymentTimerElement.textContent = `${String(paymentMinutes).padStart(2, '0')}:${String(paymentSeconds).padStart(2, '0')}`;
        }, 1000);

        // 显示模态框
        modalContainer.style.display = 'flex';

        // 关闭按钮事件
        document.querySelector('.modal-close').addEventListener('click', () => {
            modalContainer.style.display = 'none';
        });

        // 支付方式选择
        const paymentMethods = document.querySelectorAll('.payment-method');
        paymentMethods.forEach(method => {
            method.addEventListener('click', () => {
                // 移除所有active和checked类
                paymentMethods.forEach(m => {
                    m.classList.remove('active');
                    m.querySelector('.radio-btn').classList.remove('checked');
                });

                // 添加active和checked类到当前选中的方式
                method.classList.add('active');
                method.querySelector('.radio-btn').classList.add('checked');

                // 记录选中的支付方式
                const payType = method.getAttribute('data-pay-type');
                console.log('选择支付方式:', payType);
            });
        });

        // 确认支付按钮事件
        document.querySelector('.confirm-payment-btn').addEventListener('click', () => {
            // 调用结算接口
            settleOrder();
        });
    }

    // 按钮点击效果
    const buttons = document.querySelectorAll('button');
    buttons.forEach(button => {
        button.addEventListener('touchstart', () => {
            button.style.opacity = '0.8';
        });
        button.addEventListener('touchend', () => {
            button.style.opacity = '1';

            // 检查是否是底部操作栏按钮
            if (button.classList.contains('buy-alone-btn')) {
                // 单独购买按钮
                currentTeamId = ''; // 清空团ID
                // 设置购买类型标志
                window.buyType = 'alone';
                // 检查登录状态
                if (isLoggedIn) {
                    // 已登录，调用锁单接口
                    lockOrder();
                } else {
                    // 未登录，显示登录页面
                    createLoginModal();
                }
            } else if (button.classList.contains('start-group-btn')) {
                // 开团按钮
                currentTeamId = ''; // 清空团ID，表示开新团
                // 设置购买类型标志
                window.buyType = 'group';
                // 检查登录状态
                if (isLoggedIn) {
                    // 已登录，调用锁单接口
                    lockOrder();
                } else {
                    // 未登录，显示登录页面
                    createLoginModal();
                }
            }
        });
    });

});