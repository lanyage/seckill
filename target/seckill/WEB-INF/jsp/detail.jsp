<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="en">
<head>
    <title>秒杀详情页</title>
    <%--静态包含--%>
    <%@include file="common/head.jsp" %>

</head>
<body>

<div class="container">
    <div class="card text-center">
        <div class="card-header">${seckill.name}</div>
    </div>
    <div class="card-body">
        <h2 class="text-danger">
            <%--显示time图标--%>
            <span class="fa fa-clock-o"/>
            <%--展示倒计时--%>
            <span class="fa" id="seckill-box"></span>
            <span id="seckill-box"></span>
        </h2>
    </div>
</div>

<div class="modal" tabindex="-1" role="dialog" id="myModal">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">莫泰狂</h5>
            </div>
            <div class="modal-body">
                <span id="killPhoneMessage"></span>
                <input id="killPhoneKey" type="text" placeholder="please input your phone..." class="form-control">
            </div>
            <div class="modal-footer">
                <button id="killPhoneBtn" type="Submit" class="btn btn-primary">Submit</button>
            </div>
        </div>
    </div>
</div>
<!-- Optional JavaScript -->
<!-- jQuery first, then Popper.js, then Bootstrap JS -->
<script src="https://cdn.bootcss.com/jquery/3.2.1/jquery.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.3/umd/popper.min.js"
        integrity="sha384-vFJXuSJphROIrBnz7yo7oB41mKfc8JzQZiCq4NCceLEaO4IHwicKwpJf9c9IpFgh"
        crossorigin="anonymous"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta.2/js/bootstrap.min.js"
        integrity="sha384-alpBpkh1PFOepccYVYDB4do5UnbKysX5WZXm3XxPqe5iKTfUKjNkCk9SaVuEZflJ"
        crossorigin="anonymous"></script>
<%--countdown--%>
<script src="https://cdn.bootcss.com/jquery.countdown/2.2.0/jquery.countdown.min.js"></script>
<%--cookie--%>
<script src="https://cdn.bootcss.com/jquery-cookie/1.4.1/jquery.cookie.min.js"></script>
<script src="${pageContext.request.contextPath}/resource/script/seckill.js" type="application/javascript"></script>
<script type="text/javascript">
    $(function () {
        //使用EL表达式传入参数
        console.log(${seckill.seckillId})
        console.log(${seckill.startTime.time})
        console.log(${seckill.endTime.time})
        seckill.detail.init({
            seckillId: ${seckill.seckillId},
            startTime: ${seckill.startTime.time},//毫秒时间
            endTime: ${seckill.endTime.time}
        });
    });
</script>
</body>
</html>