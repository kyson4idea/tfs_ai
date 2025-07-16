package com.smy.tfs.api.service;

import com.smy.tfs.api.dto.base.Response;

import java.io.IOException;
import java.util.List;

public interface IArkMiaoDaService {
    // 同步喵达新数据
    public void syncMiaoDaNewTicketCore(String status, String st, String et, String page, String page_size);

    public void replyMiaoDaTicketCore(String snNo, String replyContent, String hideAttach, String hideContent, List<String> images, List<String> videos) throws IOException;

    /**
     * 根据executorTypeEnum和executorValue查询对应的executorList
     * 限制规则：
     * 同⼀次请求只处理⼀条投诉单的结案申请；
     * 图⽚最多上传30张，每张图⽚的⼤⼩不超过5M；
     * ⾳/视频最多上传3个，视频⼤⼩不超过100M，⾳频⼤⼩不超过10M；
     * 结案规则为1、2时，附件内容必填；
     *
     * @param snNo        结案接⼝的每⼀次调⽤仅⽀持⼀个投诉单
     * @param reason      1代表“已与⽤户沟通并达成⼀致” 2代表“联系不上⽤户” 3代表“最终解决⽅案”
     * @param solution    所提供的解决⽅案细节
     * @param hide_attach 当申诉类型为1是需要填写
     * @param images      ⽀持格式“png，jpeg，jpg"
     * @param videos      ⽀持格式“mp3，mp4”
     * @return
     */
    public void completeMiaoDaTicketCore(String snNo, String reason, String solution, String hide_attach, List<String> images, List<String> videos) throws Exception;

    /**
     * 可使⽤此接⼝对不属于您的投诉单或重复的投诉单进⾏申诉操作。需注意，只有通过detail接⼝查询过的投诉
     * 单才能申诉，否则会返回错误。
     *
     * @param sns     投诉单号，⽀持多单批量申诉。多个投诉单号中间使⽤英⽂逗号分隔
     * @param content 申诉内容
     * @param type    1代表"重复投诉" 2代表"⾮本商户投诉"
     * @param dup_sns 当申诉类型为1是需要填写
     * @param images  ⽀持格式“png，jpeg，jpg"
     * @param videos  ⽀持格式“mp3，mp4”
     * @return
     */
    public void appealMiaoDaTicketCore(String sns, String content, String type, String dup_sns, List<String> images, List<String> videos) throws Exception;

    public void updateMiaoDaTicketCore(String sn, String status_no);

    public void  updateMiaoDaTicketJob(String appid, String templateId);

    public Response<String> dispatchMiaoDaTicketCallBack(String sign, String ticketEventTag, String ticketDataId);

    public Response<String> replyMiaoDaTicketCallBack(String sign, String ticketEventTag, String ticketDataId);

    public Response<String> autoReplyMiaoDaTicketCallBack(String sign, String ticketEventTag, String ticketDataId);

    public Response<String> appealMiaoDaTicketCallBack(String sign, String ticketEventTag, String ticketDataId);

    public Response<String> completeMiaoDaTicketCallBack(String sign, String ticketEventTag, String ticketDataId);

    public Response<String> allMiaoDaTicketCallBack(String sign, String ticketEventTag, String ticketDataId);
}
