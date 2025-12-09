package cn.wenzhuo4657.dailyWeb.domain.system.model;

import cn.wenzhuo4657.dailyWeb.types.constant.Sql;

public enum DataBaseVersionVo {
    V1_0("1-0", 1),
    ;
    private String version;
    private  int code;

    DataBaseVersionVo(String version, int code) {
        this.version = version;
        this.code = code;
    }
    public  static DataBaseVersionVo getEnumByCode(int code){
        for(DataBaseVersionVo dbv:DataBaseVersionVo.values()){
            if(dbv.getCode()==code){
                return dbv;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + code);
    }
    public static DataBaseVersionVo getEnumByVersion(String value){
        for(DataBaseVersionVo dbv:DataBaseVersionVo.values()){
            if(dbv.getVersion().equals(value)){
                return dbv;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + value);
    }

    public  String getDeleteSql(){
        if (code== V1_0.getCode()){
            return Sql.deleteSql.deleteSql_1;
        }
        throw new IllegalArgumentException("No enum constant with value " + code);
    }
    public  String getInsertSql(){
        if (code== V1_0.getCode()){
            return Sql.insertSql.insertSql_1;
        }
        throw new IllegalArgumentException("No enum constant with value " + code);
    }




    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
