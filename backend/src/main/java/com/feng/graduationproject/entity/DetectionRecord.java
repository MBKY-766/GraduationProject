// src/main/java/com/feng/graduationproject/entity/DetectionRecord.java
package com.feng.graduationproject.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("detection_record")
public class DetectionRecord {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String imageName;
    private Integer defectCount;
    private String defectTypes;
    private LocalDateTime detectionTime;
    private Integer status; // 0-失败，1-成功
}