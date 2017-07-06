package com.lottery.gamble.proxy.web.service.financing;

import com.lottery.gamble.entity.FinanceCollect;
import com.lottery.gamble.entity.Result;
import com.lottery.gamble.entity.quiz.LtPaymentRecord;
import com.lottery.gamble.entity.quiz.LtProcedureFee;
import com.lottery.gamble.entity.quiz.LtWithdrawRecord;
import com.lottery.gamble.entity.quiz.PayChannels;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.util.List;

/**
 * User: zc
 * Date: 2017/6/23
 */
public interface FinancingService {

    /**
     * 获取充值渠道列表
     * @return
     */
    List<PayChannels> getPayChannel();
    /**
     * 新建渠道
     * @param payChannels
     * @return
     */
    Result newChannel(PayChannels payChannels);

    /**
     * 修改渠道
     * @param payChannels
     * @return
     */
    Result editChannel(PayChannels payChannels);

    /**
     * 应用渠道位置
     */
    Result applyChannel(Long payId, Long channelId1, Long channelId2, Long clientType1, Long clientType2);

    /**
     * 删除渠道
     * @param payId
     * @return
     */
    Result deleteChannel(Long payId);

    /**
     * 财务汇总（根据时间查询）
     * @param beginDate
     * @param endDate
     * @return
     */
    FinanceCollect getFinanceCollect(Date beginDate, Date endDate);

    /**
     * 充值记录
     * @param userName 用户账号
     * @param realName 真是姓名
     * @param proxyName 代理账号
     * @param payType 充值入口
     * @param channelType 支付渠道（对应渠道管理）
     * @param payStatus 充值进度
     * @return
     */
    Result<List<LtPaymentRecord>> getPaymentRecord(Long uid, String realName, Long proxyId, Integer payType, Integer wallet, Integer channelType, Integer payStatus, Date beginDate, Date endDate);

    /**
     * 提现记录
     * @param userName 用户账号
     * @param realName 真实姓名
     * @param proxyName 代理账号
     * @param withdrawType 充值入口
     * @param withdrawStatus 提现进度
     * @param beginDate 搜索开始时间
     * @param endDate 搜索结束时间
     * @return
     */
    Result<List<LtWithdrawRecord>> getWithdrawRecord(Long uid, String realName, Long proxyId, Integer withdrawType, Integer wallet, Integer withdrawStatus, Date beginDate, Date endDate);

    /**
     * 转账记录填写
     * @param withdrawId
     * @param bankName
     * @param bankNum
     * @param remitValue
     * @param factorage
     * @param disburseDate
     * @param transactionNum
     * @return
     */
    Result addTransferRecord(Long withdrawId, String bankName, String bankNum, Double remitValue, Double factorage, Date disburseDate, Long transactionNum);

    /**
     * 平台费，手续费后台配置
     * @param ltProcedureFee
     * @return
     */
    Result addProcedureFee(LtProcedureFee ltProcedureFee);

    Result getUserAccountInfo(Long uid, String realName, Long proxyId, Integer channelType, Integer outWallet, Date beginTime, Date endTime);

    /**
     * 根据渠道Id获取支付渠道详情
     * @param id
     * @return
     */
    PayChannels editChannelView(Long id);

    /**
     * 修改支付渠道位置
     * @param webCheckboxes
     * @param appCheckboxes
     * @return
     */
    Result updateApplyPlace(Integer webCheckboxes, Integer appCheckboxes, Long id);
}
