var checkerModalApp = new Vue({
    el: '#checkerModalApp',
    data: {
        checkerInfo: {}
    },
    created: function () {
        this.get_information();
    },
    methods: {
        update_information: function (checkerInfo) {
            if (this.checkerInfo.name !== '' && this.checkerInfo.phone !== '') {
                if (this.verification_phone()) {
                    $.ajax({
                        url: '/api/manager/update_checkerInfo',
                        type: 'POST',
                        data: JSON.stringify(checkerModalApp.checkerInfo, null, 4),
                        contentType: "application/json",
                        dataType: "json",
                        success: function (data) {
                            if (data.message === "update checkerInfo success") {
                                alert("修改成功！");
                                location.reload();
                            }
                        },
                        error: function (XMLResponse) {
                            alert(XMLResponse.responseText);
                        }
                    });
                }
            }else {
                alert("请填写完整信息！");
            }
        },
        reset_data: function () {
            this.get_information();
        },
        get_information: function () {
            $.get("/api/checker/get_checkerInfo",
                {},
                function (checkerInfo) {
                    checkerModalApp.checkerInfo = checkerInfo;
                });
        },
        verification_phone: function () {
            if (this.checkerInfo.phone.length !== 11 && this.checkerInfo.phone.length !== 6
                && this.checkerInfo.phone !== '') {
                alert("请填写有效手机号码！");
            } else {
                return true;
            }
        }
    }
});

