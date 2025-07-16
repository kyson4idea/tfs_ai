package com.smy.tfs.api.service;

import com.smy.tfs.api.dto.base.AccountInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface INCSTicketImportService {
    Map<String, Integer> importDataFromExcel(MultipartFile file, AccountInfo accountInfo);

    Map<String, Integer> importExtraDataFromExcel(MultipartFile file, AccountInfo accountInfo);

    Map<String, Integer> updateTicketValuesFromExcel(MultipartFile file);

    Map<String, Integer> reverseTicketFlowNodeData(MultipartFile file);
}
