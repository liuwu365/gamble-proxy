package com.lottery.gamble.proxy.web.service.user;

import com.lottery.gamble.dao.LtWithdrawBindingMapper;
import com.lottery.gamble.entity.Page;
import com.lottery.gamble.entity.quiz.LtWithdrawBinding;
import com.lottery.gamble.proxy.web.service.BaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Administrator on 2017/7/3.
 */
@Service
public class LtWithdrawBindingService implements BaseService<LtWithdrawBinding> {

        @Resource
        private LtWithdrawBindingMapper ltWithdrawBindingMapper;



        @Override
        public Page findPage(Page page) {

            return page;
        }


        @Override
        public List<LtWithdrawBinding> selectAll() {
            return null;
        }

        @Override
        public int deleteByPrimaryKey(Long id) {
            return ltWithdrawBindingMapper.deleteByPrimaryKey(id);
        }

        @Override
        public int insert(LtWithdrawBinding obj) {
            return ltWithdrawBindingMapper.insert(obj);
        }

        @Override
        public int insertSelective(LtWithdrawBinding obj) {
            return ltWithdrawBindingMapper.insertSelective(obj);
        }

        @Override
        public LtWithdrawBinding selectByPrimaryKey(Long id) {
            return ltWithdrawBindingMapper.selectByPrimaryKey(id);
        }

        @Override
        public int updateByPrimaryKeySelective(LtWithdrawBinding obj) {
            return ltWithdrawBindingMapper.updateByPrimaryKeySelective(obj);
        }

        @Override
        public int updateByPrimaryKey(LtWithdrawBinding obj) {
            return ltWithdrawBindingMapper.insertSelective(obj);
        }


}
