package com.smy.tfs.biz.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smy.tfs.api.dbo.TicketFormItemData;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.enums.BizResponseEnums;
import com.smy.tfs.api.service.ITicketFormItemDataService;
import com.smy.tfs.biz.mapper.TicketFormItemDataMapper;
import com.smy.tfs.common.utils.AesUtil;
import com.smy.tfs.common.utils.StringUtils;
import com.smy.tfs.common.utils.bean.ObjectHelper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * <p>
 * 工单表单组件数据表 服务实现类
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
@Service
public class TicketFormItemDataServiceImpl extends ServiceImpl<TicketFormItemDataMapper, TicketFormItemData> implements ITicketFormItemDataService {

    @Override
    public List<TicketFormItemData> selectTicketFormByDataId(String ticketDataId) {
        return this.lambdaQuery()
                .eq(TicketFormItemData::getTicketDataId, ticketDataId)
                .isNull(TicketFormItemData::getDeleteTime)
                .list();

    }

    @Override
    public List<TicketFormItemData> selectTicketFormItemByParentId(String parentId) {
        if (StringUtils.isBlank(parentId)) {
            return null;
        }
        return this.lambdaQuery()
                .like(TicketFormItemData::getItemParentId, parentId)
                .list();
    }

    @Override
    public Response<String> getPlaintextById(String id) {
        Optional<TicketFormItemData> opt = this.lambdaQuery()
                .eq(TicketFormItemData::getId, id)
                .select(TicketFormItemData::getItemConfig, TicketFormItemData::getItemConfigExt, TicketFormItemData::getTicketDataId)
                .oneOpt();
        if (!opt.isPresent())
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("根据id({})查询表单项不存在", id));
        TicketFormItemData ticketFormItemData = opt.get();
        String itemConfigStr = ticketFormItemData.getItemConfigExt();
        ticketFormItemData.EqConfig();
        JSONObject itemConfigJson = JSONObject.parseObject(itemConfigStr);
        if (ObjectHelper.isEmpty(itemConfigJson.get("isEncrypted")))
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("根据id({})查询表单项的是否加密标识不存在", id));
        Boolean isEncrypted = (Boolean) itemConfigJson.get("isEncrypted");
        if (!isEncrypted)
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("根据id({})查询表单项的是否加密标识为否", id));
        String ciphertext = (String) itemConfigJson.get("ciphertext");
        if (ObjectHelper.isEmpty(ciphertext))
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("根据id({})查询表单项的密文不存在", id));
        String plaintext;
        try {
            plaintext = AesUtil.decrypt(ciphertext);
        } catch (Exception e) {
            log.error(String.format("表单项(id为{})的密文(%s)解密失败:", id, ciphertext), e);
            return Response.error(BizResponseEnums.DES_ERROR, String.format("表单项(id为{})的密文(%s)解密失败:", id, ciphertext));
        }
        return Response.success(plaintext);
    }

    @Override
    public Response<Map<String, String>> getPlaintextMapByIds(List<String> idList) {
        List<TicketFormItemData> ticketFormItemDataList = this.lambdaQuery()
                .in(TicketFormItemData::getId, idList)
                .select(TicketFormItemData::getId, TicketFormItemData::getItemConfig, TicketFormItemData::getItemConfigExt, TicketFormItemData::getTicketDataId)
                .list();
        if (ObjectHelper.isEmpty(ticketFormItemDataList))
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("根据ids({})查询表单项不存在", idList));
        Map plaintextMap = new HashMap();
        for (TicketFormItemData ticketFormItemData : ticketFormItemDataList) {
            String id = ticketFormItemData.getId();
            String itemConfigStr = ticketFormItemData.getItemConfigExt();
            ticketFormItemData.EqConfig();
            JSONObject itemConfigJson = JSONObject.parseObject(itemConfigStr);
            if (ObjectHelper.isNotEmpty(itemConfigJson.get("isEncrypted")) && (Boolean) itemConfigJson.get("isEncrypted") && ObjectHelper.isNotEmpty(itemConfigJson.get("ciphertext"))) {
                String ciphertext = (String) itemConfigJson.get("ciphertext");
                try {
                    String plaintext = AesUtil.decrypt(ciphertext);
                    plaintextMap.put(id, plaintext);
                } catch (Exception e) {
                    log.error(String.format("表单项(id为{})的密文(%s)解密失败:", id, ciphertext), e);
                    return Response.error(BizResponseEnums.DES_ERROR, String.format("表单项(id为{})的密文(%s)解密失败:", id, ciphertext));
                }
            }
        }
        return Response.success(plaintextMap);
    }
}
