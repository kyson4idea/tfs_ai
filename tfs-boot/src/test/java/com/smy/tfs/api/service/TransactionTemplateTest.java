package com.smy.tfs.api.service;

import com.smy.framework.core.util.SequenceUtil;
import com.smy.tfs.SpringTestCase;
import com.smy.tfs.api.dbo.TicketFlowNodeApproveDetail;
import com.smy.tfs.api.enums.ApproveDealTypeEnum;
import com.smy.tfs.api.enums.TFSTableIdCode;
import com.smy.tfs.biz.service.TicketFlowNodeApproveDetailService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;

/**
 * @author z01140
 * @Package: com.smy.tfs.api.service
 * @Description:
 * @CreateDate 2024/5/9 15:18
 * @UpdateDate 2024/5/9 15:18
 */
@Slf4j
public class TransactionTemplateTest extends SpringTestCase {


    @Resource
    TransactionTemplate transactionTemplate;
    @Resource
    TicketFlowNodeApproveDetailService approveDetailService;

    @Test
    public void test(){

        TicketFlowNodeApproveDetail approveDetail = new TicketFlowNodeApproveDetail();
        String dealUserId = "000000";
        approveDetail.setTicketDataId("-1");
        approveDetail.setTicketFlowNodeDataId("-1");
        approveDetail.setDealUserId(dealUserId);
        approveDetail.setDealUserType("test");
        approveDetail.setDealUserName("test");
        approveDetail.setDealType(ApproveDealTypeEnum.PASS);
        approveDetail.setDealOpinion("test");

        transactionTemplate.executeWithoutResult((status) ->{
            approveDetailService.add(approveDetail, dealUserId);
            log.info("id1={}", approveDetail.getId());

            approveDetailService.add(approveDetail, dealUserId);
            log.info("id2={}", approveDetail.getId());
        });

        try {
            transactionTemplate.executeWithoutResult((status) ->{

                approveDetailService.add(approveDetail, dealUserId);
                log.info("id3={}", approveDetail.getId());

                approveDetailService.add(approveDetail, dealUserId);
                log.info("id4={}", approveDetail.getId());
                throw new RuntimeException("事务回滚1");
            });
        } catch (Exception e){
            log.error("", e);
        }


        transactionTemplate.execute((status) ->{

            approveDetailService.add(approveDetail, dealUserId);
            log.info("id5={}", approveDetail.getId());

            approveDetailService.add(approveDetail, dealUserId);
            log.info("id6={}", approveDetail.getId());
            return true;
        });

        transactionTemplate.execute((status) ->{

            approveDetailService.add(approveDetail, dealUserId);
            log.info("id7={}", approveDetail.getId());

            approveDetailService.add(approveDetail, dealUserId);
            log.info("id8={}", approveDetail.getId());
            status.setRollbackOnly();
            return false;
        });
    }
}
