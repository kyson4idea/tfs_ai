package com.smy.tfs.web.controller.common;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.smy.fsp.client.FileUtil;
import com.smy.tfs.api.constants.TfsBaseConstant;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.enums.BizResponseEnums;
import com.smy.tfs.biz.bo.DubboServiceConfig;
import com.smy.tfs.common.config.TfsConfig;
import com.smy.tfs.common.constant.Constants;
import com.smy.tfs.common.core.domain.AjaxResult;
import com.smy.tfs.common.core.page.TableDataInfo;
import com.smy.tfs.common.utils.DbUtil;
import com.smy.tfs.common.utils.StringUtils;
import com.smy.tfs.common.utils.file.FileUploadUtils;
import com.smy.tfs.common.utils.file.FileUtils;
import com.smy.tfs.framework.config.DynamicDubboConsumer;
import com.smy.tfs.framework.config.ServerConfig;
import lombok.val;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 通用请求处理
 *
 * @author ruoyi
 */
@RestController
@ResponseBody
public class CommonController {

    @Resource
    private DynamicDubboConsumer dynamicDubboConsumer;

    private static final Logger log = LoggerFactory.getLogger(CommonController.class);
    private static final String FILE_DELIMETER = ",";
    @Autowired
    private ServerConfig serverConfig;

    @Value("${app.env}")
    private String env;

    /**
     * 简化的构子，需要通用操作以外的操作时，可以在这里统一进行处理或分发
     */
    private static void processHook(String dataKey, String opType, JSONObject param) {
        if ("CommData".equals(dataKey)) {
            DbUtil.updateCommDataConfig();
        }
    }

    /**
     * 通用下载请求
     *
     * @param fileName 文件名称
     * @param delete   是否删除
     */
    @GetMapping("/common/download")
    public void fileDownload(String fileName, Boolean delete, HttpServletResponse response, HttpServletRequest request) {
        try {
            if (!FileUtils.checkAllowDownload(fileName)) {
                throw new Exception(StringUtils.format("文件名称({})非法，不允许下载。 ", fileName));
            }
            String realFileName = System.currentTimeMillis() + fileName.substring(fileName.indexOf("_") + 1);
            String filePath = TfsConfig.getDownloadPath() + fileName;

            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            FileUtils.setAttachmentResponseHeader(response, realFileName);
            FileUtils.writeBytes(filePath, response.getOutputStream());
            if (delete) {
                FileUtils.deleteFile(filePath);
            }
        } catch (Exception e) {
            log.error("下载文件失败", e);
        }
    }

    /**
     * 通用上传请求（单个）
     */
    @PostMapping("/common/upload")
    public AjaxResult uploadFile(MultipartFile file) throws Exception {
        try {
            // 上传文件路径
            String filePath = TfsConfig.getUploadPath();
            // 上传并返回新文件名称
            String fileName = FileUploadUtils.upload(filePath, file);
            String url = serverConfig.getUrl() + fileName;
            AjaxResult ajax = AjaxResult.success();
            ajax.put("url", url);
            ajax.put("fileName", fileName);
            ajax.put("newFileName", FileUtils.getName(fileName));
            ajax.put("originalFilename", file.getOriginalFilename());
            return ajax;
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 通用上传请求（多个）
     */
    @PostMapping("/common/uploads")
    public AjaxResult uploadFiles(List<MultipartFile> files) throws Exception {
        try {
            // 上传文件路径
            String filePath = TfsConfig.getUploadPath();
            List<String> urls = new ArrayList<String>();
            List<String> fileNames = new ArrayList<String>();
            List<String> newFileNames = new ArrayList<String>();
            List<String> originalFilenames = new ArrayList<String>();
            for (MultipartFile file : files) {
                // 上传并返回新文件名称
                String fileName = FileUploadUtils.upload(filePath, file);
                String url = serverConfig.getUrl() + fileName;
                urls.add(url);
                fileNames.add(fileName);
                newFileNames.add(FileUtils.getName(fileName));
                originalFilenames.add(file.getOriginalFilename());
            }
            AjaxResult ajax = AjaxResult.success();
            ajax.put("urls", StringUtils.join(urls, FILE_DELIMETER));
            ajax.put("fileNames", StringUtils.join(fileNames, FILE_DELIMETER));
            ajax.put("newFileNames", StringUtils.join(newFileNames, FILE_DELIMETER));
            ajax.put("originalFilenames", StringUtils.join(originalFilenames, FILE_DELIMETER));
            return ajax;
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 通用上传请求（多个）
     */
    @PostMapping("/common/uploadFilesToFsp")
    public AjaxResult uploadFilesToFsp(List<MultipartFile> files) throws Exception {
        List<String> uploadUrlList = new ArrayList<>();
        for (MultipartFile file : files) {
            String fspFileUrl = FileUtil.uploadBuilder()
                    .fileName(file.getOriginalFilename())
                    .fileByte(file.getBytes())
                    .sceneType(TfsBaseConstant.FSP_UPLOAD_SCENE_TYPE)
                    .upload();
            // 返回https地址
            if ("prd".equals(env) || "pre".equals(env)) {
                fspFileUrl = fspFileUrl.replaceAll("http://", "https://");
            }
            uploadUrlList.add(fspFileUrl);
        }
        return AjaxResult.success(uploadUrlList);
    }

    @PostMapping({"/common/downloadFileFromFsp","/outside/common/downloadFileFromFsp"})
    public void downloadFileFromFsp(@Param("fileName") String fileName, @Param("fileUrl") String fileUrl, HttpServletResponse response) {
        try {
            if (StrUtil.hasBlank(fileName, fileUrl)) {
                throw new Exception("附件下载失败：文件名称或文件地址不能为空");
            }
            byte[] fileBytes = FileUtil.downloadBuilder()
                    .urlPath(fileUrl)
                    .download();

            //文件名称编码
            String encodedFileName = Base64.getEncoder().encodeToString(fileName.getBytes(StandardCharsets.UTF_8));
            encodedFileName = encodedFileName.replace("+", "-").replace("/", "_");

            response.setContentType("application/x-download");
            int dotIndex = fileName.lastIndexOf(".");
            if (dotIndex > 0) {
                String suffix = fileName.substring(dotIndex + 1).toLowerCase();
                if (suffix.equals("png")) {
                    response.setContentType("image/png");
                } else if (suffix.equals("jpg") || suffix.equals("jpeg")) {
                    response.setContentType("image/jpeg");
                } else if (suffix.equals("gif")) {
                    response.setContentType("image/gif");
                } else if (suffix.equals("tiff")) {
                    response.setContentType("image/tiff");
                } else if (suffix.equals("bmp")) {
                    response.setContentType("application/x-bmp");
                } else if (suffix.equals("ico")) {
                    response.setContentType("image/x-icon");
                } else if (suffix.equals("txt")) {
                    response.setContentType("text/plain");
                } else if (suffix.equals("pdf")) {
                    response.setContentType("application/pdf");
                } else {
                    response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"; filename*=UTF-8''" + encodedFileName);
                    response.setContentType("application/octet-stream");
                }
            } else {
                response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"; filename*=UTF-8''" + encodedFileName);
                response.setContentType("application/octet-stream");
            }

            response.addHeader("Content-Length", String.valueOf(fileBytes.length));
            try (OutputStream os = response.getOutputStream()) {
                os.write(fileBytes, 0, fileBytes.length);
                os.flush();
            } catch (Exception e) {
                log.error("读取文件失败", e);
                throw e;
            }
        } catch (Exception e) {
            log.error("异常", e);
        }
    }


    /**
     * 本地资源通用下载
     */
    @GetMapping("/common/download/resource")
    public void resourceDownload(String resource, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        try {
            if (!FileUtils.checkAllowDownload(resource)) {
                throw new Exception(StringUtils.format("资源文件({})非法，不允许下载。 ", resource));
            }
            // 本地资源路径
            String localPath = TfsConfig.getProfile();
            // 数据库资源地址
            String downloadPath = localPath + StringUtils.substringAfter(resource, Constants.RESOURCE_PREFIX);
            // 下载名称
            String downloadName = StringUtils.substringAfterLast(downloadPath, "/");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            FileUtils.setAttachmentResponseHeader(response, downloadName);
            FileUtils.writeBytes(downloadPath, response.getOutputStream());
        } catch (Exception e) {
            log.error("下载文件失败", e);
        }
    }

    /**
     * 通用接口-拉取列表
     */
    @GetMapping("/common/data/list/{dataKey}")
    public TableDataInfo dataList(@PathVariable String dataKey, @RequestParam Map<String, String> reqParam) throws Exception {
        // 取出sql模板，并生成正式sql
        JSONObject param = JSONObject.from(reqParam);
        JSONObject sqlResult = DbUtil.generateSql(dataKey, "LIST", param);
        if (null == sqlResult) {
            return TableDataInfo.fail(500, "数据查询未授权，请联系管理员!");
        }

        String sql = sqlResult.getString("sql");
        List<Object> sqlParam = (List<Object>) sqlResult.get("param");

        // 生成count的sql
        int idx = sql.indexOf("FROM");
        String countSql = "SELECT count(1) " + sql.substring(idx);

        // 增加limit
        String pageNum = param.getString("pageNum");
        String pageSize = param.getString("pageSize");
        if (null != pageNum && null != pageSize) {
            int iNum = Integer.valueOf(pageNum);
            int iSize = Integer.valueOf(pageSize);
            sql += (" limit " + (iNum - 1) * iSize + "," + iSize);
        }

        int total = DbUtil.dbQueryInt(countSql, sqlParam.toArray());
        List<Map<String, Object>> rows = DbUtil.dbQueryList(sql, sqlParam.toArray());
        return TableDataInfo.success(rows, total);
    }

    /**
     * 通用接口-新增
     */
    @PostMapping("/common/data/{dataKey}")
    public AjaxResult dataAdd(@PathVariable String dataKey, @RequestBody String body) throws Exception {
        JSONObject param = JSONObject.parseObject(body);

        JSONObject sqlResult = DbUtil.generateSql(dataKey, "ADD", param);
        if (null == sqlResult) {
            return AjaxResult.error(500, "数据操作未授权，请联系管理员!");
        }

        String sql = sqlResult.getString("sql");
        List<Object> sqlParam = (List<Object>) sqlResult.get("param");

        DbUtil.dbUpdate(sql, sqlParam.toArray());

        processHook(dataKey, "ADD", param);

        return AjaxResult.success();
    }

    /**
     * 通用接口-更新
     */
    @PutMapping("/common/data/{dataKey}")
    public AjaxResult dataUpdate(@PathVariable String dataKey, @RequestBody String body) throws Exception {
        JSONObject param = JSONObject.parseObject(body);

        JSONObject sqlResult = DbUtil.generateSql(dataKey, "UPDATE", param);
        if (null == sqlResult) {
            return AjaxResult.error(500, "数据操作未授权，请联系管理员!");
        }

        String sql = sqlResult.getString("sql");
        List<Object> sqlParam = (List<Object>) sqlResult.get("param");

        DataSourceTransactionManager tm = new DataSourceTransactionManager(DbUtil.getDataSource());
        // 事务定义类
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = tm.getTransaction(def);

        int rows = DbUtil.dbUpdate(sql, sqlParam.toArray());

        if (rows > 1) {
            tm.rollback(status);
            return AjaxResult.error(500, "数据操作出错");
        } else {
            tm.commit(status);
            processHook(dataKey, "UPDATE", param);
        }

        return AjaxResult.success();
    }

    /**
     * 通用接口-删除
     */
    @DeleteMapping("/common/data/{dataKey}/{keyValue}")
    public AjaxResult dataDelete(@PathVariable String dataKey, @PathVariable String keyValue) throws Exception {
        JSONObject param = new JSONObject();
        String priKey = DbUtil.getExecuteSqlPriKey(dataKey, "DELETE");
        param.put(priKey, keyValue);

        JSONObject sqlResult = DbUtil.generateSql(dataKey, "DELETE", param);
        if (null == sqlResult) {
            return AjaxResult.error(500, "数据操作未授权，请联系管理员!");
        }

        String sql = sqlResult.getString("sql");
        List<Object> sqlParam = (List<Object>) sqlResult.get("param");

        DataSourceTransactionManager tm = new DataSourceTransactionManager(DbUtil.getDataSource());
        // 事务定义类
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = tm.getTransaction(def);

        int rows = DbUtil.dbUpdate(sql, sqlParam.toArray());
        if (rows == 0) {
            tm.rollback(status);
            return AjaxResult.error(500, "数据操作出错");
        } else {
            tm.commit(status);
            processHook(dataKey, "DELETE", param);
        }

        return AjaxResult.success();
    }

    //纯转发，用于前端获取外部数据数据源
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/common/redirect")
    public AjaxResult redirect(String url, String type, String tag, HttpServletResponse response) {
        if ("dubbo".equals(type)) {
            try {
                Response<DubboServiceConfig> dubboServiceConfigRsp = DubboServiceConfig.parseStrToDubboConfig(url);
                if (!dubboServiceConfigRsp.isSuccess()) {
                    return AjaxResult.error("调用失败", Response.error(dubboServiceConfigRsp.getEnum(), dubboServiceConfigRsp.getMsg()));
                }
                DubboServiceConfig dubboServiceConfig = dubboServiceConfigRsp.getData();
                Object invokeResult = dynamicDubboConsumer.invokeDubboService(dubboServiceConfig.getInterfaceName(), dubboServiceConfig.getMethodName(), new Object[]{tag}, dubboServiceConfig.getVersion(), dubboServiceConfig.getGroup());
                return AjaxResult.success("调用成功", Response.success(invokeResult));
            } catch (Exception ex) {
                return AjaxResult.error("执行错误", new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("DUBBO 转发异常:%s", ex.getMessage())));
            }
        }
        return AjaxResult.error("请求参数错误", Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "不支持的转发类型:" + type));
    }


    private final RestTemplate restTemplate = new RestTemplate();

    @RequestMapping("/common/redirectHttp")
    public AjaxResult proxyRequest(HttpMethod method, HttpServletRequest request, String url) throws IOException {
        String targetUrl = url;

        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            if ("accept-encoding".equals(headerName)) {
                continue;
            }
            headers.add(headerName, request.getHeader(headerName));
        }
        headers.add("content-type", "application/json");
        headers.add("accept", "application/json, text/plain, */*");
        headers.add("charset", "UTF-8");

        val body = StreamUtils.copyToByteArray(request.getInputStream());

        ResponseEntity<?> response = restTemplate.exchange(
                targetUrl,
                method,
                new org.springframework.http.HttpEntity<>(body, headers),
                String.class
        );
        // return new ResponseEntity<>(response.getBody(), response.getHeaders(), response.getStatusCode());
        return AjaxResult.success("操作成功", JSONObject.parseObject(Objects.requireNonNull(response.getBody()).toString()));
    }
}
