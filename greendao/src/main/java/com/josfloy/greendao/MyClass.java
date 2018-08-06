package com.josfloy.greendao;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class MyClass {
    public static void main(String[] args) {
        //创建Schema对象，构造方法第一个参数为数据库版本号
        //第二个参数为自动生成的实体类将要存放的位置
        Schema schema = new Schema(1000, "com.josfloy.greendao.entity.greendao");
        //添加需要创建的实体类信息
        addNote(schema);

        try {
            //创建实体类，第二个参数填Android Module的路径
            new DaoGenerator().generateAll(schema, "./app/src/main/java");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加将要创建的实体类的信息，会根据类名生成数据库的表，属性名生成数据库的字段
     * 当然如果想建多张表，可以创建多个Entity 对象
     */
    private static void addNote(Schema schema) {
        //指定需要生成实体类的类名，类名根据表命自动命名
        Entity entity = schema.addEntity("wisdomEntity");
        //指定自增长主键
        entity.addIdProperty().autoincrement().primaryKey();
        //添加类的属性，根据属性生成数据库表中的字段
        entity.addStringProperty("english");
        entity.addStringProperty("china");

        //指定需要生成实体类的类名，类名确定了那么表名也是根据这个类名来自动命名的
        Entity entity1 = schema.addEntity("CET4Entity");
        entity1.addIdProperty().autoincrement().primaryKey();
        entity1.addStringProperty("word");
        entity1.addStringProperty("english");
        entity1.addStringProperty("china");
        entity1.addStringProperty("sign");
    }
}
