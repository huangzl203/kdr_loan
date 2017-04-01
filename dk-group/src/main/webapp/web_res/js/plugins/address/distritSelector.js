function DistritSelector(opts) {
    $.extend(this, {
        value: [],
        curValue: [],
        curText: [],
        data: null,
        target: null,
        listTarget: null,
        curType: 0,
        text:null,
        input:null,
		level:3,
        callback: function() {},
        template: '<section class="side-selector hide">' +
            '    <div class="selector-wrapper">' +
            '        <h3><span class="icon-back2"></span>'+(opts.title||'请选择')+'</h3>' +
            '        <div class="selector-list">' +
            '            <ul>' +
            '            </ul>' +
            '        </div>' +
            '    </div>' +
            '</section>'
    }, opts || {});
    this.init();
};

DistritSelector.prototype = {
    init: function() {
        this.curValue = this.value;
        this.target = $(this.template);
        $(document.body).append(this.target);
        this.listTarget = this.target.find('.selector-list ul');
        this.events();
        if(this.value){
        	this.input.val(this.value.join(','));
        	this.text.text(this.data[this.value[0]].n+'-'+this.data[this.value[0]][this.value[1]].n+(this.data[this.value[0]].d?'':'-'+this.data[this.value[0]][this.value[1]][this.value[2]].n));
        }
        this.createProvince();
    },
    show: function() {
        var that = this;
        this.target.removeClass('hide');
        setTimeout(function() {
            that.target.addClass('active');
        }, 0);
    },
    close: function() {
        var that = this;
        this.target.removeClass('active');
        setTimeout(function() {
            that.target.addClass('hide');
        }, 310);
    },
    createProvince: function() {
        var html = '';
        for (var key in this.data) {
            html += '<li' + (key != this.curValue[0] ? '' : ' class="selected"') + ' data-type="0" data-id="' + key + '">' + this.data[key].n + '</li>';
        }
        this.listTarget.html(html);
        this.curType = 0;
    },
    createCity: function() {
        var html = '',
            data = this.data[this.curValue[0]];
        for (var key in data) {
            if (/c\d+/.test(key))
                html += '<li' + (key != this.curValue[1] ? '' : ' class="selected"') + ' data-type="1" data-id="' + key + '">' + data[key].n + '</li>';
        }
        this.listTarget.html(html);
        this.curType = 1;
    },
    ceateCountry: function() {
        var html = '',
            data = this.data[this.curValue[0]][this.curValue[1]];
        for (var key in data) {
            if (/c\d+/.test(key))
                html += '<li' + (key != this.curValue[2] ? '' : ' class="selected"') + ' data-type="2" data-id="' + key + '">' + data[key].n + '</li>';
        }
        this.listTarget.html(html);
        this.curType = 2;
    },
    events: function() {
        var that = this,
            type,
            id,
            name;
        this.listTarget.delegate('li', 'click', function() {
            type = $(this).data('type') | 0;
            id = $(this).data('id');
            name = $(this).text();
            switch (type) {
                case 0:
                    that.curValue = [];
                    that.curText = [];
                    that.curValue[0] = id;
                    that.curText[0] = name;
                    that.createCity();
                    break;
                case 1:
                    that.curValue[1] = id;
                    that.curText[1] = name;
                    that.curValue = that.curValue.slice(0, 2);
                    that.curText = that.curText.slice(0,2);
                    if (that.data[that.curValue[0]].d||that.level==2) {
                        that.callback(that.curValue, that.curText);
                        $(this).addClass('selected').siblings().removeClass();
                        that.close();
                    } else {
                        that.ceateCountry();
                    }
                    break;
                case 2:
                    that.curValue[2] = id;
                    that.curText[2] = name;
                    that.callback(that.curValue, that.curText);
                    $(this).addClass('selected').siblings().removeClass();
                    that.close();
                    break;
            }
        });
        this.target.delegate('.icon-back2', 'click', function() {
            switch (that.curType) {
                case 0:
                    that.close();
                    break;
                case 1:
                    that.curType = 0;
                    that.createProvince();
                    break;
                case 2:
                    that.curType = 1;
                    that.createCity();
                    break;
            }
        });
    }
};
