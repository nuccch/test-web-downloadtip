<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>流式方式下载数据时展示提示信息</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <style>
        button {
            border: none;
            padding: 10px 22px;
            text-align: center;
            text-decoration: none;
            display: inline-block;
            font-size: 14px;
            margin: 4px 2px;
        }

        table{
            width: 100%;
            border-collapse: collapse;
        }

        table caption{
            font-size: 1em;
            font-weight: bold;
            margin: 1em 0;
        }

        th,td{
            border: 1px solid #999;
            text-align: center;
            padding: 10px 0;
        }

        table thead tr{
            background-color: #008c8c;
            color: #fff;
        }

        table tbody tr:nth-child(odd){
            background-color: #eee;
        }

        table tbody tr:hover{
            background-color: #ccc;
        }

        table tfoot tr td{
            text-align: right;
            padding-right: 10px;
        }
    </style>
    <script type="text/javascript" src="http://cdn.bootcss.com/jquery/1.11.3/jquery.min.js"></script>
    <script type="text/javascript" src="http://cdn.bootcss.com/jquery.blockUI/2.66.0-2013.10.09/jquery.blockUI.min.js"></script>
</head>
<body>
    <h1>流式方式下载数据时展示提示信息</h1>
    <div>
        <form id="paramForm">
            <input type="hidden" name="name" value="zhangsan" />
            <input type="hidden" name="age" value="12" />
        </form>
    </div>
    <div>
        <button id="btnDownloadXhr">导出</button>
    </div>
    <div>
        <table>
            <thead>
            <tr>
                <th>编号</th>
                <th>姓名</th>
                <th>年龄</th>
                <th>住址</th>
                <th>身份ID</th>
            </tr>
            </thead>
            <tbody>
            </tbody>
            <c:forEach var="data" items="${page}" varStatus="loop">
                <tr>
                    <td>${loop.index+1}</td>
                    <td>${data.name}</td>
                    <td>${data.age}</td>
                    <td>${data.addr}</td>
                    <td>${data.number}</td>
                </tr>
            </c:forEach>
        </table>
    </div>
</body>
<script type="text/javascript">
    $(function(){
        $("#btnDownloadXhr").click(function () {
            downloadXhr();
        })
    })

    function downloadXhr() {
        var url = "/download";
        var param = $("#paramForm").serialize();
        var xhr = new XMLHttpRequest();
        xhr.open("POST", url);
        xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        xhr.responseType = "blob";
        xhr.onload = function () {
            console.log("加载完毕");
            $.unblockUI();
            if (xhr.status === 200) {
                var blob = this.response;
                var reader = new FileReader();
                reader.readAsDataURL(blob);
                var filename = decodeURI(this.getResponseHeader('filename'));
                reader.onload = function (e) {
                    var a = document.createElement('a');
                    a.download = filename;
                    a.href = e.target.result;
                    //兼容firefox
                    $('body').append(a);
                    a.click();
                    $(a).remove();
                }
            }
        };
        xhr.send(param);
        console.log("正在加载数据...")
        $.blockUI({
            message: "正在加载数据..."
        });
    }
</script>
</html>
