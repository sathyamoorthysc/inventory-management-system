package com.cams.inventorymanagement.adapter.rest.filter;

import static com.cams.inventorymanagement.adapter.rest.Constant.MDC_REQUEST_ID_KEY;

import com.cams.inventorymanagement.common.util.UuidGenerator;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

/**
 * Filter to generate and set a unique trace_id for each request. The trace_id is stored in the MDC
 * (Mapped Diagnostic Context) for logging purposes. We can also add specific information from http
 * request like product id to the MDC if needed using handler interceptors and AOP (Aspect-Oriented
 * Programming).
 */
@Component
@RequiredArgsConstructor
public class TraceIdFilter implements Filter {
  private final UuidGenerator uuidGenerator;

  @Override
  public void doFilter(
      ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
      throws IOException, ServletException {
    try {
      MDC.put(MDC_REQUEST_ID_KEY, uuidGenerator.generate().toString());
      filterChain.doFilter(servletRequest, servletResponse);
    } finally {
      MDC.remove(MDC_REQUEST_ID_KEY);
    }
  }
}
