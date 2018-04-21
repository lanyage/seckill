/**
 * Created by lanyage on 2016/3/18.
 */
//存放主要交互逻辑js代码
//javascript 模块化，java中使用分包
var seckill = {

    //封装秒杀相关ajax的地址，url
    URL: {
        now: function () {
            return '/seckill/time/now';
        },
        exposer: function (seckillId) {
            return '/seckill/' + seckillId + "/exposer";
        },
        execution: function (seckillId, md5) {
            return '/seckill/' + seckillId + '/' + md5 + '/execution';
        }
    },

    //用来验证手机号是否是正确的手机号
    validateUserPhone: function (userPhone) {
        if (userPhone && userPhone.length == 11 && !isNaN(Number(userPhone))) {
            return true;
        } else {
            return false;
        }
    },

    //处理秒杀,也就是秒杀的条件成熟的时候就显示秒杀按钮
    handlerSeckill: function (seckillId, seckillBox) {

        //url, 参数, function(result)
        $.post(seckill.URL.exposer(seckillId), {}, function (result) {
            //在回调函数中执行交互流程
            if (result && result['success']) {
                var exposer = result['data'];
                if (exposer['exposed']) {
                    //如果秒杀开始了
                    //显示秒杀按钮
                    seckillBox.hide()
                        .html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>').show();
                    //获取秒杀地址
                    var md5 = exposer['md5'];
                    var killUrl = seckill.URL.execution(seckillId, md5);

                    //绑定一次点击事件
                    $('#killBtn').one('click', function () {
                        //执行秒杀请求的操作
                        //禁用按钮
                        $(this).addClass('disabled');
                        //发送秒杀的请求,执行秒杀
                        $.post(killUrl, {}, function (result) {
                            if (result && result['success']) {
                                var killResult = result['data'];
                                var state = killResult['state'];
                                var stateInfo = killResult['stateInfo'];
                                seckillBox.html('<span class="label label-success">' + stateInfo + '</span>');
                            }
                        });
                    });
                } else {
                    //还没开始秒杀,机器之间的偏差
                    var now = exposer['nowTime'];
                    var start = exposer['startTime'];
                    var end = exposer['endTime'];
                    seckill.countdown(seckillId, now, start, end);
                }
            }
        });
    },
    countdown: function (seckillId, nowTime, startTime, endTime) {
        var seckillBox = $("#seckill-box");
        //时间判断
        if (nowTime > endTime) {
            seckillBox.html('秒杀结束');
        } else if (nowTime < startTime) {
            //秒杀未开始,计时事件绑定,这东西就要进行误差分析了,1秒机器误差
            var killTime = new Date(startTime + 1000);
            seckillBox.countdown(killTime, function (event) {
                //时间格式,这个好屌
                var format = event.strftime('秒杀倒计时: %D天 %H时 %M分 %S秒');
                seckillBox.html(format);

                //时间完成后回调时间
            }).on('finish.countdown', function () {
                //获取秒杀地址,控制显示逻辑，执行秒杀
                seckill.handlerSeckill(seckillId, seckillBox);
            });
        } else {
            //秒杀开始
            seckill.handlerSeckill(seckillId, seckillBox);
        }
    },

    //详情页秒杀逻辑
    detail: {
        //详情页初始化
        /*
         * seckillId: ${seckill.seckillId},
         startTime: ${seckill.startTime.time},//毫秒时间
         endTime: ${seckill.endTime.time}
         * */
        init: function (params) {
            //用户手机验证和登录,做计时交互
            //规划我们的交互路程
            //因为没有后端，所以我们把用户信息放在cookie当中
            console.log(params);
            var userPhone = $.cookie('userPhone');

            //验证手机号
            /*如果手机号不存在或者手机号不正确,弹出模态框*/
            if (!seckill.validateUserPhone(userPhone)) {
                //绑定userPhone
                //控制输出
                var seckillPhoneModal = $("#myModal");
                //modal组件有自己的方法,显示弹出层,设置modal的隐藏属性
                seckillPhoneModal.modal({
                    show: true,//显示弹出层
                    backdrop: 'static',//禁止位置关闭
                    keyboard: false//关闭键盘事件
                });
                $("#killPhoneBtn").click(function () {
                    var inputPhone = $("#killPhoneKey").val();
                    /*如果手机是符合规范的,那么就把手机号存到cookie中*/
                    if (seckill.validateUserPhone(inputPhone)) {
                        //写入电话到cookie,只在seckill下有效,cookie的有效日期为7天,并且有效路径为/seckill
                        $.cookie('userPhone', inputPhone, {expires: 7, path: '/seckill'});
                        //刷新当前页面,会重新走刚刚的流程
                        window.location.reload();
                    } else {
                        $('#killPhoneMessage').hide().html('<label class="label label-danger">手机号错误!</label>').show(1000);
                    }
                });
            }
            //已经登录,也就是手机号通过了验证
            //计时交互
            var seckillId = params['seckillId'];
            var startTime = params['startTime'];
            var endTime = params['endTime'];
            /*之所以获取当前时间还要从后端获,是因为前端数据是可以篡改的,不安全,只是为了改善用户使用体验*/
            $.get(seckill.URL.now(), {}, function (result) {
                if (result && result['success']) {
                    var nowTime = result['data'];
                    //时间判断,计时服务时间判断，计时交互
                    seckill.countdown(seckillId, nowTime, startTime, endTime);
                } else {
                    console.log('result:' + result);
                }
            });
        }
    }
}