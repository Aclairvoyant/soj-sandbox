package com.sjdddd.sojsandbox.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 进程执行信息
 *
 * @author 沈佳栋
 * @Since 2024/3/11 19:28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExecuteMessage {

    private Integer exitValue;

    private String message;

    private String errorMessage;

    private Long time;

    private Long memory;

}
