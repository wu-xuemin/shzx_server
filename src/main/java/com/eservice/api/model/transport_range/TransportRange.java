package com.eservice.api.model.transport_range;

import javax.persistence.*;

@Table(name = "transport_range")
public class TransportRange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 区间的站点名称，JSON

[{"station_name": "AA路口"} ,
{ "station_name": "BB口"},
{ "station_name": "CC口"}
]

     */
    private String stations;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取区间的站点名称，JSON

[{"station_name": "AA路口"} ,
{ "station_name": "BB口"},
{ "station_name": "CC口"}
]

     *
     * @return stations - 区间的站点名称，JSON

[{"station_name": "AA路口"} ,
{ "station_name": "BB口"},
{ "station_name": "CC口"}
]

     */
    public String getStations() {
        return stations;
    }

    /**
     * 设置区间的站点名称，JSON

[{"station_name": "AA路口"} ,
{ "station_name": "BB口"},
{ "station_name": "CC口"}
]

     *
     * @param stations 区间的站点名称，JSON

[{"station_name": "AA路口"} ,
{ "station_name": "BB口"},
{ "station_name": "CC口"}
]

     */
    public void setStations(String stations) {
        this.stations = stations;
    }

    /**
     * 区间名称
     */
    private String rangeName;

    public String getRangeName() {
        return rangeName;
    }

    public void setRangeName(String rangeName) {
        this.rangeName = rangeName;
    }


}