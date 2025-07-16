package com.smy.tfs.framework.web.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smy.tfs.common.utils.AesUtil;
import com.smy.tfs.common.utils.ServletUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author smy
 */
public class RawDataHttpMessageConverter extends AbstractJackson2HttpMessageConverter {

    public static final MediaType ACCEPT_MEDIA_TYPE = MediaType.parseMediaType("application/json");

    public RawDataHttpMessageConverter(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    /**
     * Returns {@code true} if any of the {@linkplain #setSupportedMediaTypes(List)
     * supported} media types {@link MediaType#includes(MediaType) include} the
     * given media type.
     *
     * @param mediaType the media type to read, can be {@code null} if not specified.
     *                  Typically the value of a {@code Content-Type} header.
     * @return {@code true} if the supported media types include the media type,
     * or if the media type is {@code null}
     */
    @Override
    protected boolean canRead(MediaType mediaType) {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return false;
        }

        HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();
        Boolean isEncrypt = ServletUtils.isEncryptData(request);
        return isEncrypt;
    }

    /**
     * Returns {@code true} if the given media type includes any of the
     * {@linkplain #setSupportedMediaTypes(List) supported media types}.
     *
     * @param mediaType the media type to write, can be {@code null} if not specified.
     *                  Typically the value of an {@code Accept} header.
     * @return {@code true} if the supported media types are compatible with the media type,
     * or if the media type is {@code null}
     */
    @Override
    protected boolean canWrite(MediaType mediaType) {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return false;
        }

        HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();
        Boolean isEncrypt = ServletUtils.isEncryptData(request);
        return isEncrypt;
    }

    /**
     * Read an object of the given type form the given input message, and returns it.
     *
     * @param type         the (potentially generic) type of object to return. This type must have
     *                     previously been passed to the {@link #canRead canRead} method of this interface,
     *                     which must have returned {@code true}.
     * @param contextClass a context class for the target type, for example a class
     *                     in which the target type appears in a method signature (can be {@code null})
     * @param inputMessage the HTTP input message to read from
     * @return the converted object
     * @throws IOException                     in case of I/O errors
     * @throws HttpMessageNotReadableException in case of conversion errors
     */
    @Override
    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return super.read(type, contextClass, new InternalHttpInputMessage(inputMessage));
    }

    /**
     * @param clazz        the type of object to return
     * @param inputMessage the HTTP input message to read from
     * @return the converted object
     * @throws IOException                     in case of I/O errors
     * @throws HttpMessageNotReadableException in case of conversion errors
     */
    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return super.readInternal(clazz, new InternalHttpInputMessage(inputMessage));
    }

    /**
     * @param object        the object to write to the output message
     * @param type          the type of object to write (may be {@code null})
     * @param outputMessage the HTTP output message to write to``
     * @throws IOException                     in case of I/O errors
     * @throws HttpMessageNotWritableException in case of conversion errors
     */
    @Override
    protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        // convert internal real object into RawData
        String encodeString = "";
        try {
            encodeString = AesUtil.encrypt(getObjectMapper().writeValueAsString(object));
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.writeInternal(new RawData(encodeString), RawData.class, outputMessage);
    }

    protected InputStream convert(InputStream inputStream, HttpInputMessage httpInputMessage) throws IOException {
        MediaType contentType = httpInputMessage.getHeaders().getContentType();
        Charset charset = getCharset(contentType);
        String data = getObjectMapper().readValue(inputStream, RawData.class).getData();
        String decodeString = "";
        if (data != null) {
            try {
                decodeString = AesUtil.decrypt(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new ByteArrayInputStream(decodeString.getBytes(charset));
    }

    /**
     * wrap {@link HttpInputMessage} to convert internal {@link RawData} into real objects
     *
     * @author smy
     */
    private class InternalHttpInputMessage implements HttpInputMessage {

        private final HttpInputMessage delegator;

        private InternalHttpInputMessage(HttpInputMessage delegator) {
            this.delegator = delegator;
        }

        @Override
        public InputStream getBody() throws IOException {
            return convert(delegator.getBody(), this);
        }

        @Override
        public HttpHeaders getHeaders() {
            return delegator.getHeaders();
        }
    }
}

