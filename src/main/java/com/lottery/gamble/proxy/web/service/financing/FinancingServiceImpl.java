package com.lottery.gamble.proxy.web.service.financing;

import com.google.gson.Gson;
import com.lottery.gamble.dao.*;
import com.lottery.gamble.entity.BackUser;
import com.lottery.gamble.entity.FinanceCollect;
import com.lottery.gamble.entity.Result;
import com.lottery.gamble.entity.quiz.LtPaymentRecord;
import com.lottery.gamble.entity.quiz.LtWithdrawBinding;
import com.lottery.gamble.entity.quiz.LtWithdrawRecord;
import com.lottery.gamble.entity.quiz.PayChannels;
import com.lottery.gamble.entity.quiz.*;
import com.lottery.gamble.entity.user.UserInfo;
import com.lottery.gamble.enums.financing.PayChannelLimitEnum;
import com.lottery.gamble.enums.financing.PayClientEnum;
import com.lottery.gamble.enums.financing.PayTypeEnum;
import com.lottery.gamble.enums.financing.WithdrawStatusEnum;
import com.lottery.gamble.proxy.core.util.CheckUtil;
import com.lottery.gamble.proxy.web.controller.base.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * User: zc
 * Date: 2017/6/23
 */
@Service
public class FinancingServiceImpl implements FinancingService {

    Logger logger = LoggerFactory.getLogger(FinancingServiceImpl.class);

    private static final Gson gson = new Gson();

    private static final String TIME = "次";

    private static final String MONEY = "万";

    @Resource
    private PayChannelsMapper payChannelsMapper;

    @Resource
    private LtPaymentRecordMapper ltPaymentRecordMapper;

    @Resource
    private LtWithdrawRecordMapper ltWithdrawRecordMapper;

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private LtWithdrawBindingMapper ltWithdrawBindingMapper;

    @Resource
    private LtProcedureFeeMapper ltProcedureFeeMapper;

    @Resource
    private LtUserAccountMapper ltUserAccountMapper;

    @Override
    public List<PayChannels> getPayChannel() {
        List<PayChannels> payChannelsList = payChannelsMapper.selectAll();
        if (CheckUtil.isEmpty(payChannelsList)){
            return payChannelsList;
        }
        for (PayChannels payChannels : payChannelsList){
            if (payChannels.getPayLimitType() == PayChannelLimitEnum.TIME_TYPE.getCode()){
                payChannels.setLimit(PayChannelLimitEnum.TIME_TYPE.getDesc() + payChannels.getPayLimitNum() + TIME);
            }else if (payChannels.getPayLimitType() == PayChannelLimitEnum.ALL_MONEY_TYPE.getCode()){
                payChannels.setLimit(PayChannelLimitEnum.ALL_MONEY_TYPE.getDesc() + payChannels.getPayLimitNum() + MONEY);
            }else if (payChannels.getPayLimitType() == PayChannelLimitEnum.ONCE_MONET_TYPE.getCode()){
                payChannels.setLimit(PayChannelLimitEnum.ONCE_MONET_TYPE.getDesc() + payChannels.getPayLimitNum() + MONEY);
            }

            if (payChannels.getPayType().equals(PayTypeEnum.ALIPAY.getCode())){
                payChannels.setPayChannel(PayTypeEnum.ALIPAY.getDesc());
            }else if (payChannels.getPayType().equals(PayTypeEnum.WECHAT.getCode())){
                payChannels.setPayChannel(PayTypeEnum.WECHAT.getDesc());
            }else if (payChannels.getPayType().equals(PayTypeEnum.BANK_CARD.getCode())){
                payChannels.setPayChannel(PayTypeEnum.BANK_CARD.getDesc());
            }
        }
        return payChannelsList;
    }

    @Override
    public Result newChannel(PayChannels payChannels) {
        if (payChannels.getPayChannelsName() == null){
            return new Result(400 ,"请输入充值渠道名称！");
        }
        if (payChannels.getPayLimitType() == null){
            return new Result(400 ,"请选择限制类型！");
        }
        if (payChannels.getPayLimitNum() == null){
            return new Result(400 ,"请输入限制次数！");
        }
        if (payChannels.getPayType() == null){
            return new Result(400 ,"请选择充值渠道类型！");
        }
        if (payChannels.getTransferAccount() == null){
            return new Result(400 ,"请完善转账账户！");
        }

        int count = payChannelsMapper.insert(payChannels);
        if (count > 0){
            return new Result(200 ,"添加充值渠道成功！");
        }

        return new Result(500 ,"添加充值渠道失败！");
    }

    @Override
    public Result editChannel(PayChannels payChannels) {

        if (payChannels.getPayChannelsName() == null){
            return new Result(400 ,"请输入充值渠道名称！");
        }
        if (payChannels.getPayLimitType() == null){
            return new Result(400 ,"请选择限制类型！");
        }

        if (payChannels.getPayType() == null){
            return new Result(400 ,"请选择充值渠道类型！");
        }
        if (payChannels.getTransferAccount() == null){
            return new Result(400 ,"请完善转账账户！");
        }

        if (payChannels.getPayLimitType().equals(PayChannelLimitEnum.TIME_TYPE.getCode())){

        }else if (payChannels.getPayLimitType().equals(PayChannelLimitEnum.ALL_MONEY_TYPE.getCode())){
            payChannels.setPayLimitNum(payChannels.getPayLimitNum2());
            payChannels.setPayRemind(payChannels.getPayRemind2());
        }else if (payChannels.getPayLimitType().equals(PayChannelLimitEnum.ONCE_MONET_TYPE.getCode())){
            payChannels.setPayLimitNum(payChannels.getPayLimitNum3());
        }

        if (payChannels.getPayLimitNum3() != null){
            payChannels.setPayLimitNum(payChannels.getPayLimitNum3());
            payChannels.setPayRemind(null);
        }
        int count = payChannelsMapper.updateByPrimaryKey2(payChannels);
        if (count > 0){
            return new Result(200 ,"修改充值渠道成功");
        }
        return new Result(20000 ,"修改充值渠道失败！");
    }

    @Override
    public Result applyChannel(Long payId, Long channelId1, Long channelId2, Long clientType1, Long clientType2) {
        PayChannels payChannels = new PayChannels();
        PayChannels pay = null;
        PayChannels pay1 = null;
        payChannels.setId(payId);
        if (clientType1.equals(PayClientEnum.WEB.getType())){
            payChannels.setWebLocation(Long.valueOf(channelId1).intValue());
            pay = payChannelsMapper.selectWebByClientType(Long.valueOf(channelId1).intValue());
        }
        if (clientType2.equals(PayClientEnum.APP.getType())){
            payChannels.setAppLocation(Long.valueOf(channelId2).intValue());
            pay1 = payChannelsMapper.selectAppByClientType(Long.valueOf(channelId2).intValue());
        }
        int count = payChannelsMapper.updateByPrimaryKeySelective(payChannels);
        if (count < 0){
            return new Result(500, "设置应用渠道失败！");
        }
        if (!pay.equals(null)){
            pay.setWebLocation(null);
            payChannelsMapper.updateByPrimaryKeySelective(pay);
        }
        if (!pay1.equals(null)){
            pay.setAppLocation(null);
            payChannelsMapper.updateByPrimaryKeySelective(pay1);
        }
        return new Result(200, "设置应用渠道成功！");
    }

    @Override
    public Result deleteChannel(Long payId) {
        int count = payChannelsMapper.deleteByPrimaryKey(payId);
        if (count > 0){
            return new Result(200, "删除渠道成功！");
        }
        return new Result(500, "删除渠道失败！");
    }

    @Override
    public Result<List<LtPaymentRecord>> getPaymentRecord(Long uid, String realName, Long proxyId,
                                                          Integer payType, Integer wallet, Integer channelType, Integer payStatus, Date beginDate, Date endDate) {
        UserInfo userInfo = userInfoMapper.selectByPrimaryKey(uid);
        if (CheckUtil.isEmpty(userInfo)){
            return new Result<>(404, "用户名不存在！");
        }

        List<Long> userInfoList = userInfoMapper.selectByRealName(realName);
        if (!userInfoList.contains(userInfo.getId())){
            return new Result<>(404, "用户资料不匹配！");
        }

        UserInfo proxyUserInfo = userInfoMapper.selectByPrimaryKey(proxyId);
        if (CheckUtil.isEmpty(proxyUserInfo)) {
            return new Result<>(404, "代理资料不匹配！");
        }
        List<LtPaymentRecord> list = ltPaymentRecordMapper.selectRecord(uid, proxyId,
                channelType, payType, wallet, payStatus, beginDate, endDate);
        Result result = new Result();
        result.setCode(200);
        result.setT(list);
        return result;
    }

    @Override
    public Result<List<LtWithdrawRecord>> getWithdrawRecord(Long uid, String realName, Long proxyId,
                                                            Integer withdrawType, Integer wallet, Integer withdrawStatus, Date beginDate, Date endDate) {
        UserInfo userInfo = userInfoMapper.selectByPrimaryKey(uid);
        if (CheckUtil.isEmpty(userInfo)){
            return new Result<>(404, "用户名不存在！");
        }

        List<Long> userInfoList = userInfoMapper.selectByRealName(realName);
        if (!userInfoList.contains(userInfo.getId())){
            return new Result<>(404, "用户资料不匹配！");
        }

        UserInfo proxyUserInfo = userInfoMapper.selectByPrimaryKey(proxyId);
        if (CheckUtil.isEmpty(proxyUserInfo)){
            return new Result<>(404, "代理资料不匹配！");
        }
        LtWithdrawBinding ltWithdrawBinding = ltWithdrawBindingMapper.selectByUid(userInfo.getId());
        List<LtWithdrawRecord> ltWithdrawRecordList = ltWithdrawRecordMapper.selectWithdrawRecord(uid, proxyId,
                withdrawType, withdrawStatus, wallet, beginDate, endDate);
        if (!CheckUtil.isEmpty(ltWithdrawBinding)){
            for (LtWithdrawRecord ltWithdrawRecord : ltWithdrawRecordList){
                ltWithdrawRecord.setApplyBank(ltWithdrawBinding.getBank());
                ltWithdrawRecord.setApplyBankNum(ltWithdrawBinding.getBankNumber());
                ltWithdrawRecord.setApplyBankRealName(ltWithdrawBinding.getBankRealName());
            }
        }
        Result result = new Result();
        result.setCode(200);
        result.setT(ltWithdrawRecordList);
        return result;
    }

    @Override
    public Result addTransferRecord(Long withdrawId, String bankName, String bankNum,
                                    Double remitValue, Double factorage, Date disburseDate, Long transactionNum) {
        if (CheckUtil.isEmpty(withdrawId)){
            return new Result(400, "参数错误！");
        }
        if (CheckUtil.isEmpty(bankName)){
            return new Result(400, "请选择银行！");
        }
        if (CheckUtil.isEmpty(bankNum)){
            return new Result(400, "请输入正确的银行卡号！");
        }
        if (CheckUtil.isEmpty(remitValue)){
            return new Result(400, "请输入转账金额！");
        }
        if (CheckUtil.isEmpty(factorage)){
            return new Result(400, "请输入手续费！");
        }
        if (CheckUtil.isEmpty(transactionNum)){
            return new Result(400, "请输入转账交易号！");
        }
        BigDecimal bigDecimal = new BigDecimal(remitValue);
        LtWithdrawRecord ltWithdrawRecord = new LtWithdrawRecord();
        ltWithdrawRecord.setwId(withdrawId);
        ltWithdrawRecord.setwBank(bankName);
        ltWithdrawRecord.setwBankNum(bankNum);
        ltWithdrawRecord.setwValue(bigDecimal);
        ltWithdrawRecord.setwPoundage(factorage);
        ltWithdrawRecord.setwUpdateDate(disburseDate);
        ltWithdrawRecord.setwTransferId(transactionNum);
        int count = ltWithdrawRecordMapper.insertSelective(ltWithdrawRecord);
        if (count < 0){
            return new Result(500, "添加失败！");
        }
        ltWithdrawRecordMapper.updateStatus(withdrawId, WithdrawStatusEnum.REMIT.getCode());
        return new Result(200, "转账成功！");
    }

    @Override
    public Result addProcedureFee(LtProcedureFee ltProcedureFee) {
        if (CheckUtil.isEmpty(ltProcedureFee.getPaymentProcedureFee())){
            return new Result(400, "请输入充值手续费比例！");
        }
        if (CheckUtil.isEmpty(ltProcedureFee.getWithdrawProcedureFee())){
            return new Result(400, "请输入提现手续费比例！");
        }
        if (CheckUtil.isEmpty(ltProcedureFee.getTerraceFee())){
            return new Result(400, "请输入平台费比例！");
        }
        BackUser currentUser = BaseController.getCurrentUser();
        ltProcedureFee.setOperator(currentUser.getUserName());
        int count = ltProcedureFeeMapper.insertSelective(ltProcedureFee);
        if (count < 0){
            return new Result(500, "添加配置失败！");
        }
        return new Result(200, "添加成功！");
    }

    @Override
    public Result getUserAccountInfo(Long uid, String realName, Long proxyId, Integer channelType, Integer outWallet, Date beginTime, Date endTime) {

        UserInfo userInfo = userInfoMapper.selectByPrimaryKey(uid);
        if (CheckUtil.isEmpty(userInfo)){
            return new Result<>(404, "用户名不存在！");
        }

        List<Long> userInfoList = userInfoMapper.selectByRealName(realName);
        if (!userInfoList.contains(userInfo.getId())){
            return new Result<>(404, "用户资料不匹配！");
        }

        UserInfo proxyUserInfo = userInfoMapper.selectByPrimaryKey(proxyId);
        if (CheckUtil.isEmpty(proxyUserInfo)){
            return new Result<>(404, "代理资料不匹配！");
        }

        List<LtUserAccount> userAccountList = ltUserAccountMapper.selectByCondition(uid, proxyId, channelType, outWallet, beginTime, endTime);
        if (userAccountList != null) {
            for (LtUserAccount ltUserAccount : userAccountList){
                ltUserAccount.setRealName(realName);
            }
        }
        Result result = new Result();
        result.setCode(200);
        result.setT(userAccountList);
        return result;
    }

    @Override
    public PayChannels editChannelView(Long id) {
        PayChannels payChannels = payChannelsMapper.selectByPrimaryKey(id);
        return payChannels;
    }

    @Override
    public Result updateApplyPlace(Integer webCheckboxes, Integer appCheckboxes, Long id) {
        PayChannels payChannels = new PayChannels();
        payChannels.setAppLocation(appCheckboxes);
        payChannels.setWebLocation(webCheckboxes);
        payChannels.setId(id);
        int count = payChannelsMapper.updateByPrimaryKeySelective(payChannels);
        if (count > 0){
            return new Result(200, "修改成功！");
        }
        return new Result(20000, "修改失败！");
    }

    @Override
    public FinanceCollect getFinanceCollect(Date beginDate, Date endDate) {
        FinanceCollect financeCollect = new FinanceCollect();
        int count = ltPaymentRecordMapper.selectCollectByPayDate(beginDate, endDate);
        Double paymentSum = ltPaymentRecordMapper.selectPaymentSum(beginDate, endDate);
        Double paymentPoundage = ltPaymentRecordMapper.selectPaymentPoundage(beginDate, endDate);
        int count1 = ltWithdrawRecordMapper.selectCollectByUpdateDate(beginDate, endDate);
        Double withdrawSum = ltWithdrawRecordMapper.selectWithdrawSum(beginDate, endDate);
        Double withdrawPoundage = ltWithdrawRecordMapper.selectWithdrawPoundage(beginDate, endDate);

        financeCollect.setPaymentNum(count)
                .setPaymentSum(paymentSum.toString())
                .setPaymentPoundage(paymentPoundage.toString())
                .setWithdrawNum(count1)
                .setWithdrawSum(withdrawSum.toString())
                .setWithdrawPoundage(withdrawPoundage.toString());

        return financeCollect;
    }
}
