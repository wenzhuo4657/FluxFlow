package cn.wenzhuo4657.dailyWeb.types.constant;

public class Sql {
    public  static  class  deleteSql{


//        todo 这些sql，考虑使用模版字符串动态替换数据库名称

      public static String deleteSql_1 = """
                    
                    DELETE FROM main.docs_item;
                    
                    DELETE FROM main.docs_type;
                    
                    DELETE FROM main.docs;
                    
                    DELETE FROM main.user;
                    
                    DELETE FROM main.user_auth;
                    
            """;

    }


    public  static  class insertSql{

        public static String insertSql_1 = """
                   
                   INSERT INTO main.docs_type (id, name, des, type_id)
                   SELECT id, name, des, type_id FROM tempDb.docs_type;
                   
                   INSERT INTO main.docs_item (id, "index", docs_id, item_content, item_Field)
                   SELECT id, "index", docs_id, item_content, item_Field FROM tempDb.docs_item;

                  

                   INSERT INTO main.docs (id, name, type_id, create_time, update_time, docs_id, user_id)
                   SELECT id, name, type_id, create_time, update_time, docs_id, user_id FROM tempDb.docs;

       

                   INSERT INTO main."user" (id, user_id, oauth_userId, oauth_provider, created_at, name, avatar_url)
                   SELECT id, user_id, oauth_userId, oauth_provider, created_at, name, avatar_url FROM tempDb."user";

                   INSERT INTO main.user_auth (id, user_id, docs_type_id)
                   SELECT id, user_id, docs_type_id FROM tempDb.user_auth;

          
                   """;

    }

}
