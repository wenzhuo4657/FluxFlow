package cn.wenzhuo4657.noifiterBot.app.types.cache;

public enum CacheType{
    LOCAL("local","本地缓存"),
    REDIS("redis","Redis缓存"),
    VALKEY("valkey","ValKey缓存");


    String name;
    String description;

    CacheType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }


    public static CacheType valueOfByName(String name){
        for(CacheType cacheType:CacheType.values()){
            if(cacheType.getName().equals(name)){
                return cacheType;
            }
        }
        throw new IllegalArgumentException("No enum constant " + name);
    }


}