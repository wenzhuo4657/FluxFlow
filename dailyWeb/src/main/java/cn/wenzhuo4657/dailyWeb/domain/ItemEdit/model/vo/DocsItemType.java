package cn.wenzhuo4657.dailyWeb.domain.ItemEdit.model.vo;


/**
 * ItemType遵循ContentTyp表中类型，
 */
public   class   DocsItemType{

//        根据ItemType生成对应的Filed属性

        private static DocsItemFiled.ItemFiled[] Daily_Base_Field=
                new DocsItemFiled.ItemFiled[]{ DocsItemFiled.ItemFiled.data
        };

        private static DocsItemFiled.ItemFiled[] Plan_I_Field=
                new DocsItemFiled.ItemFiled[]{
             DocsItemFiled.ItemFiled.status,
             DocsItemFiled.ItemFiled.time_point,
             DocsItemFiled.ItemFiled.title
        };

        private static  DocsItemFiled.ItemFiled[] Plan_II_Field=
                new DocsItemFiled.ItemFiled[]{
             DocsItemFiled.ItemFiled.status,
             DocsItemFiled.ItemFiled.data_start,
             DocsItemFiled.ItemFiled.data_end,
             DocsItemFiled.ItemFiled.title
        };

        private static  DocsItemFiled.ItemFiled[]  StickyNote_Field={};


        public  enum  ItemType{
            dailyBase("dailyBase", Daily_Base_Field,0),
            Plan_I("Plan_I", Plan_I_Field,1),
            Plan_II("Plan_II", Plan_II_Field,2),
            StickyNote("StickyNote",StickyNote_Field,3)
            ;

            ItemType(String typeName, DocsItemFiled.ItemFiled[] filed,int code) {
                this.typeName = typeName;
                this.filed = filed;
                this.code=code;
            }

            private String typeName;

            private DocsItemFiled.ItemFiled[] filed;
            private int code;

            public static ItemType valueOfByCode(int type) throws ClassNotFoundException {
                for (ItemType item : ItemType.values()) {
                    if (item.getCode() == type) {
                        return item;
                    }
                }
                throw new ClassNotFoundException("No enum constant " + type);
            }

            public int getCode() {
                return code;
            }

            public void setCode(int code) {
                this.code = code;
            }

            public String getTypeName() {
                return typeName;
            }

            public void setTypeName(String typeName) {
                this.typeName = typeName;
            }

            public DocsItemFiled.ItemFiled[] getFiled() {
                return filed;
            }

            public void setFiled(DocsItemFiled.ItemFiled[] filed) {
                this.filed = filed;
            }

            public static ItemType valueOfByName(String name) throws ClassNotFoundException {
                for(ItemType item:ItemType.values()){
                    if(item.getTypeName().equals(name)){
                        return item;
                    }
                }
                throw new ClassNotFoundException("No enum constant " + name);
            }
        }


}