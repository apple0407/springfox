package com.mangofactory.swagger.readers.operation.parameter;

import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.isBlank;

public class ParameterNameReader implements Command<RequestMappingContext> {
   @Override
   public void execute(RequestMappingContext context) {
      MethodParameter methodParameter = (MethodParameter) context.get("methodParameter");
      ApiParam apiParam = methodParameter.getParameterAnnotation(ApiParam.class);
      String name = methodParameter.getParameterName();
      if (null != apiParam && !isBlank(apiParam.name())) {
         name = apiParam.name();
      } else {
         name = findParameterName(methodParameter);
      }
      context.put("name", name);
   }

   private String findParameterName(MethodParameter methodParameter) {
      Annotation[] methodAnnotations = methodParameter.getParameterAnnotations();
      if (null != methodAnnotations) {
         for (Annotation annotation : methodAnnotations) {
            if (annotation instanceof PathVariable) {
               return ((PathVariable) annotation).value();
            } else if (annotation instanceof ModelAttribute) {
               return ((ModelAttribute) annotation).value();
            } else if (annotation instanceof RequestParam) {
               return ((RequestParam) annotation).value();
            } else if (annotation instanceof RequestHeader) {
               return ((RequestHeader) annotation).value();
            }
         }
      }
      String parameterName = methodParameter.getParameterName();
      return isNullOrEmpty(parameterName) ? format("param%s", methodParameter.getParameterIndex()) : parameterName;
   }

   public static List safeList(List other) {
      return other == null ? Collections.EMPTY_LIST : other;
   }
}
