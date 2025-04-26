package com.middle.wcs.order.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author 文亮
 * @description 报表查询入参
 * @date 2023-06-2023/6/24-0:40
 */
@Data
public class OrderInfoPageDTO {

    /**
     * 起始页数
     */
    @NotNull(message = "起始页数不能为空")
    private Integer pageNum;

    /**
     * 每页大小
     */
    @NotNull(message = "每页大小")
    private Integer pageSize;
}
