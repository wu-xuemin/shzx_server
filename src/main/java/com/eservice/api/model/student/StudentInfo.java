package com.eservice.api.model.student;

public class StudentInfo extends Student {

    private String banjiName;

    public String getBanjiName() {
        return banjiName;
    }

    public void setBanjiName(String banjiName) {
        this.banjiName = banjiName;
    }

    private String busNumber;

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    private String boardStationMorningName;

    public void setBoardStationMorningName(String boardStationMorningName) {
        this.boardStationMorningName = boardStationMorningName;
    }

    public String getBoardStationMorningName() {
        return boardStationMorningName;
    }

    private String boardStationAfternoonName;

    public String getBoardStationAfternoonName() {
        return boardStationAfternoonName;
    }

    public void setBoardStationAfternoonName(String boardStationAfternoonName) {
        this.boardStationAfternoonName = boardStationAfternoonName;
    }
}
