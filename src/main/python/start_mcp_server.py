from mysql_mcp_server import server

if __name__ == "__main__":
    # 创建 Server 实例，参数根据实际改
    s = server.Server(
        host="172.29.206.129",
        port=3307,
        mysql_user="test",
        mysql_password="528492",
        mysql_database="demo"
    )
    # 调用实例的 serve 方法
    s.serve()
