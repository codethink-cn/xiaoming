package cn.chuanwise.xiaoming.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class XiaomingRuntimeException extends RuntimeException {
    String message;
}
