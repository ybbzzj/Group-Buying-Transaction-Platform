package com.codezj.infrastructure.adapter.repository;

import com.codezj.domain.tag.adapter.repository.ITagRepository;
import com.codezj.domain.tag.model.entity.CrowdTagsJobEntity;
import com.codezj.infrastructure.dao.ICrowdTagsDao;
import com.codezj.infrastructure.dao.ICrowdTagsDetailDao;
import com.codezj.infrastructure.dao.ICrowdTagsJobDao;
import com.codezj.infrastructure.dao.po.CrowdTags;
import com.codezj.infrastructure.dao.po.CrowdTagsDetail;
import com.codezj.infrastructure.dao.po.CrowdTagsJob;
import com.codezj.infrastructure.redis.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBitSet;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 标签仓储实现类
 */
@Slf4j
@Repository
public class TagRepository implements ITagRepository {

    @Resource
    private ICrowdTagsDao crowdTagsDao;

    @Resource
    private ICrowdTagsDetailDao crowdTagsDetailDao;

    @Resource
    private ICrowdTagsJobDao crowdTagsJobDao;

    @Resource
    private IRedisService redisService;

    @Override
    public CrowdTagsJobEntity queryCrowdTagsJob(String tagId, String batchId) {
        CrowdTagsJob crowdTagsJobReq = CrowdTagsJob.builder().tagId(tagId).batchId(batchId).build();
        CrowdTagsJob crowdTagsJob = crowdTagsJobDao.queryCrowdTagsJob(crowdTagsJobReq);
        if (Objects.isNull(crowdTagsJob)) {
            log.error("[标签仓储服务]标签任务不存在，tagId:{}, batchId:{}", tagId, batchId);
            return null;
        }
        CrowdTagsJobEntity crowdTagsJobEntity = CrowdTagsJobEntity.builder()
                .tagType(crowdTagsJob.getTagType())
                .tagRule(crowdTagsJob.getTagRule())
                .status(crowdTagsJob.getStatus())
                .statStartTime(crowdTagsJob.getStatStartTime())
                .tagType(crowdTagsJob.getTagType())
                .build();
        log.info("[标签仓储服务]查询标签任务，tagId:{}, batchId:{}, crowdTagsJobEntity:{}", tagId, batchId, crowdTagsJobEntity);
        return crowdTagsJobEntity;
    }

    @Override
    public int addCrowdTagsDetail(String tagId, String userId) {
        int add = 0;
        CrowdTagsDetail crowdTagsDetailReq = CrowdTagsDetail.builder()
            .tagId(tagId)
            .userId(userId)
            .build();
        try {
            // 添加用户到redis
            // 这里使用BitMap这种数据结构
            RBitSet bitSet = redisService.getBitSet(tagId);
            // 注意：这里注意需要将userId映射为long
            bitSet.set(redisService.getIndexFromUserId(userId), true);

            add = crowdTagsDetailDao.addCrowdTagsUserId(crowdTagsDetailReq);



            log.info("[标签仓储服务]添加标签人群，tagId:{}, crowd:{}", tagId, userId);
        } catch (DuplicateKeyException ignore) {
            log.info("[标签仓储服务]{}标签已添加人群{}", tagId, userId);
        }
        return add;

    }

    @Override
    public void updateCrowdTagsStatistics(String tagId, int count) {
        CrowdTags crowdTagsReq = CrowdTags.builder().tagId(tagId).statistics(count).build();
        crowdTagsDao.updateCrowdTagsStatistics(crowdTagsReq);
        log.info("[标签仓储服务]更新标签统计计数，tagId: {}，增加人数: {}", tagId, count);
    }
}
