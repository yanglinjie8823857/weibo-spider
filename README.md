# weibo-spider
新浪微博爬虫
1.导入工程（在conf/spider.txt文件中修改数据库信息，验证码图片保存路径，爬取数据类型）
2.配置好mysql数据库（已提供.sql文件，保证数据库中user表中任一条记录的israd属性不为0，该表用于爬取时登录新浪微博获取cookie）
3.保持有网状态
4.运行weibospiderstarter文件(控制台会显示输入验证码，按照验证码填写即可开始爬取)
