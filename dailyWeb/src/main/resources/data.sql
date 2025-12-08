
INSERT INTO "docs_type" (id, type_id, name, des)
SELECT 0, 0, 'dailyBase', '基本日报类型：以日期 格式分割content_item数据返回前端展示 '
    WHERE NOT EXISTS (SELECT 1 FROM "docs_type" WHERE id = 0);


-- INSERT INTO "docs_type" (id, type_id, name, des)
-- SELECT 1, 1, 'Plan_I', '定时任务类型 '
--     WHERE NOT EXISTS (SELECT 1 FROM "docs_type" WHERE id = 1);
--
-- INSERT INTO "docs_type" (id, type_id, name, des)
-- SELECT 2, 2, 'Plan_II', '长期任务类型'
--     WHERE NOT EXISTS (SELECT 1 FROM "docs_type" WHERE id = 2);



