package com.eservice.api.model.transport_record;

import com.alibaba.fastjson.annotation.JSONField;

import java.sql.Timestamp;
import java.time.DateTimeException;
import java.util.Date;
import javax.persistence.*;

@Table(name = "transport_record")
public class TransportRecord {
    /**
     * 接送班次信息，一趟对应一条记录，一趟可以对应多条学生接送信息 pickedStudentInfo
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 接送日期
     */
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date date;

    /**
     * 接送模式，分为“上午接”，“下午送”，“晚班”
     */
//    private String mode;

    /**
     * 校车线路，外键，可以根据校车去查应乘学生
     */
    private Integer busLine;

    /**
     * 校车所处的当前站点
     */
    @Column(name = "current_station")
    private Integer currentStation;

    /**
     * 获取接送信息
     *
     * @return id - 接送信息
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置接送信息
     *
     * @param id 接送信息
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取接送日期
     *
     * @return date - 接送日期
     */
    public Date getDate() {
        return date;
    }

    /**
     * 设置接送日期
     *
     * @param date 接送日期
     */
    public void setDate(Date date) {
        this.date = date;
    }

//    /**
//     * 获取接送模式，分为“上午接”，“下午送”，“晚班”
//     *
//     * @return mode - 接送模式，分为“上午接”，“下午送”，“晚班”
//     */
//    public String getMode() {
//        return mode;
//    }

    /**
     * 设置接送模式，分为“上午接”，“下午送”，“晚班”
     *
     * @param mode 接送模式，分为“上午接”，“下午送”，“晚班”
     */
//    public void setMode(String mode) {
//        this.mode = mode;
//    }

    /**
     * 获取校车，外键，可以根据校车去查应乘学生
     *
     * @return bus - 校车，外键，可以根据校车去查应乘学生
     */
    public Integer getBusLine() {
        return busLine;
    }

    /**
     * 设置校车，外键，可以根据校车去查应乘学生
     *
     * @param busLine 校车，外键，可以根据校车去查应乘学生
     */
    public void setBusLine(Integer busLine) {
        this.busLine = busLine;
    }

    /**
     * 获取校车所处的当前站点
     *
     * @return current_station - 校车所处的当前站点
     */
    public Integer getCurrentStation() {
        return currentStation;
    }

    /**
     * 设置校车所处的当前站点
     *
     * @param currentStation 校车所处的当前站点
     */
    public void setCurrentStation(Integer currentStation) {
        this.currentStation = currentStation;
    }

    /**
     * 早上发车、午班发车、晚班发车
     * 根据时间判断这3个，午班上车和晚班上车另外处理
     */
    @Column(name = "flag")
    private String flag;

    /**
     * 校车编号，因为晚班的线路和校车不绑定，所以需要记录校车  --待废弃
     */
    @Column(name = "bus_number_in_tr")
    private String busNumberInTR;

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getBusNumberInTR() {
        return busNumberInTR;
    }

    public void setBusNumberInTR(String busNumberInTR) {
        this.busNumberInTR = busNumberInTR;
    }

    /**
     * 行程进行中(TRANSPORT_RECORD_STATUS_RUNNING)、已结束
     */
    @Column(name = "status")
    private String status;

    // Timestamp 可以显示非0时分秒
    @Column(name = "begin_time")
    private Timestamp beginTime;

    @Column(name = "end_time")
    private Timestamp endTime;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Timestamp beginTime) {
        this.beginTime = beginTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }
}