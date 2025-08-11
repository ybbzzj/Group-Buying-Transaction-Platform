package com.codezj.domain.tag.service;

import com.codezj.domain.tag.adapter.repository.ITagRepository;
import com.codezj.domain.tag.model.entity.CrowdTagsJobEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 人群标签服务
 */
@Slf4j
@Service
public class TagService implements ITagService {

    @Resource
    private ITagRepository repository;

    @Override
    public void executeTagJob(String tagId, String batchId) {
        // 获取人群标签任务
        CrowdTagsJobEntity crowdTagsJob = repository.queryCrowdTagsJob(tagId, batchId);

        // todo bzj 执行任务，采集人群标签

        List<String> userIdList = new ArrayList<String>(){{
            add("zhangsan");
            add("lisi");
            add("wangwu");
            add("zhaoliu");
            add("dingdongji");
            add("wuwu");
            add("momo");
            add("gege");
            add("dada");
            add("xixi");
            add("haha");
            add("hehe");
            add("jiji");
            add("kuke");
            add("lulu");
            add("nini");
            add("ouou");
            add("pupu");
            add("qiqu");
            add("renren");
            add("shishi");
            add("tutu");
            add("wewe");
            add("xixi");
            add("yiyi");
            add("zhezi");
        }};

        // 落库
        int statistics = 0;
        for (String userId : userIdList) {
            statistics += repository.addCrowdTagsDetail(tagId, userId);
        }

        // 更新计数
        repository.updateCrowdTagsStatistics(tagId, statistics);
    }
}
