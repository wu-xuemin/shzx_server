webpackJsonp([25],{KzIj:function(t,a,e){"use strict";Object.defineProperty(a,"__esModule",{value:!0});var n,s=e("BO1k"),i=e.n(s),l=e("vt++"),r=e("V5TU"),o=e("weGg"),m=(e("0xDb"),e("IcnI")),c={name:"MaintainDetail",props:{machineNameplate:{type:String,default:""},maintainRecorderInfo:{type:Object,default:{}}},data:function(){return n=this,{loading:{},formData:{},activeTabId:0,maintainLibList:[],maintainCotentList:[],maintainTypeList:[],skillStars:{},statusList:l.a.MaintainStatusList,maintainMembers:[]}},filters:{filterStatus:function(t){for(var a="",e=0;e<n.statusList.length;e++)if(t==n.statusList[e].value){a=n.statusList[e].name;break}return a},filterDateString:function(t){return isStringEmpty(t)?"":new Date(t).format("yyyy-MM-dd")},filterMaintainTypeName:function(t){for(var a="",e=0;e<n.maintainTypeList.length;e++)if(t==n.maintainTypeList[e].id){a=n.maintainTypeList[e].maintainTypeName;break}return a}},computed:{isShowAgent:{get:function(){return n.activeTabId=isStringEmpty(n.formData.machineAgentName)?"2":"1",!isStringEmpty(n.formData.machineAgentName)}},isShowSinsim:{get:function(){return!(m.a.getters.user.user.agent>0)&&isStringEmpty(n.formData.machineAgentName)}}},methods:{onStarLoad:function(t){return Object(o.c)(t.starMode)},showCheckBox:function(t){return 1==t?"checkbox_checked":"checkbox_unchecked"},loadData:function(){this.formData={},null!=n.machineNameplate&&(this.formData=copyObject(n.maintainRecorderInfo)),n.maintainCotentList=[];try{n.maintainCotentList=JSON.parse(this.formData.maintainInfo)}catch(t){console.log(t)}},tabSwitchClick:function(t){console.log("tabSwitchClick:"+t)},loadMaintainTypeList:function(){Object(r.j)({page:"",size:""}).then(function(t){responseIsOK(t)?n.maintainTypeList=t.data.data.list:showMSG(n,isStringEmpty(t.data.message)?"查询数据失败！":t.data.message)})},loadMaintainMembers:function(){n.maintainMembers=[];var t={page:"",size:"",maintainRecordId:n.formData.id};Object(r.h)(t).then(function(t){responseIsOK(t)?n.maintainMembers=t.data.data.list:showMSG(n,isStringEmpty(t.data.message)?"查询数据失败！":t.data.message)})},loadMaintainLib:function(){Object(r.k)({maintainType:"1",maintainLibName:n.formData.maintainLibName,page:"",size:""}).then(function(t){responseIsOK(t)?(n.mainTainLibList=t.data.data.list,n.mainTainLibList.forEach(function(t){var a=!1,e=!0,s=!1,l=void 0;try{for(var r,o=i()(n.maintainCotentList);!(e=(r=o.next()).done);e=!0){var m=r.value;m.maintainType==t.maintainType&&(m.contentList.push({content:t.maintainContent,checkValue:0}),a=!0)}}catch(t){s=!0,l=t}finally{try{!e&&o.return&&o.return()}finally{if(s)throw l}}if(!a){var c={maintainType:t.maintainType,contentList:[]};c.contentList.push({content:t.maintainContent,checkValue:0}),n.maintainCotentList.push(c)}})):showMSG(n,isStringEmpty(t.data.message)?"查询数据失败！":t.data.message)})}},mounted:function(){n.loadMaintainTypeList(),n.loadData(),0==n.maintainCotentList.length&&n.loadMaintainLib(),n.loadMaintainMembers(),this.skillStars=Object(o.d)(n.formData.maintainFeedbackCustomerMark)}},f={render:function(){var t=this,a=t.$createElement,e=t._self._c||a;return e("div",{staticClass:"root_div"},[e("el-form",{attrs:{model:t.formData,"label-position":"right","label-width":"100px"}},[e("div",{staticClass:"panel panel-primary"},[e("div",{staticClass:"panel-heading",staticStyle:{"text-align":"left"}},[e("h3",{staticClass:"panel-title"},[t._v("客户信息")])]),t._v(" "),e("div",{staticClass:"panel-body",staticStyle:{"margin-left":"-20px"}},[e("el-col",{attrs:{span:6}},[e("el-form-item",{attrs:{label:"客户:"}},[e("span",{domProps:{innerHTML:t._s(t.formData.machineCustomerName)}})])],1),t._v(" "),e("el-col",{attrs:{span:6}},[e("el-form-item",{attrs:{label:"联系方式:"}},[e("span",{domProps:{innerHTML:t._s(t.formData.machineCustomerPhone)}})])],1),t._v(" "),e("el-col",{attrs:{span:6}},[e("el-form-item",{attrs:{label:"代理商:"}},[e("span",{domProps:{innerHTML:t._s(t.formData.machineAgentName)}})])],1),t._v(" "),e("el-col",{attrs:{span:6}},[e("el-form-item",{attrs:{label:"联系方式:"}},[e("span",{domProps:{innerHTML:t._s(t.formData.machineAgentPhone)}})])],1),t._v(" "),e("el-col",{attrs:{span:6}},[e("el-form-item",{attrs:{label:"出厂日期:"}},[e("span",[t._v(t._s(t._f("filterDateString")(t.formData.facoryDate)))])])],1)],1)]),t._v(" "),e("div",{staticClass:"panel panel-primary"},[e("div",{staticClass:"panel-heading",staticStyle:{"text-align":"left"}},[e("h3",{staticClass:"panel-title"},[t._v("机器信息")])]),t._v(" "),e("div",{staticClass:"panel-body",staticStyle:{"margin-left":"-20px"}},[e("el-col",{attrs:{span:6}},[e("el-form-item",{attrs:{label:"机器编号:"}},[e("span",{domProps:{innerHTML:t._s(t.formData.machineNameplate)}})])],1),t._v(" "),e("el-col",{attrs:{span:6}},[e("el-form-item",{attrs:{label:"机型:"}},[e("span",{domProps:{innerHTML:t._s(t.formData.machineType)}})])],1),t._v(" "),e("el-col",{attrs:{span:6}},[e("el-form-item",{attrs:{label:"针数:"}},[e("span",{domProps:{innerHTML:t._s(t.formData.needleNum)}})])],1),t._v(" "),e("el-col",{attrs:{span:6}},[e("el-form-item",{attrs:{label:"头数:"}},[e("span",{domProps:{innerHTML:t._s(t.formData.headNum)}})])],1),t._v(" "),e("el-col",{attrs:{span:6}},[e("el-form-item",{attrs:{label:"头距:"}},[e("span",{domProps:{innerHTML:t._s(t.formData.headDistance)}})])],1),t._v(" "),e("el-col",{attrs:{span:6}},[e("el-form-item",{attrs:{label:"X行程:"}},[e("span",{domProps:{innerHTML:t._s(t.formData.xDistance)}})])],1),t._v(" "),e("el-col",{attrs:{span:6}},[e("el-form-item",{attrs:{label:"Y行程:"}},[e("span",{domProps:{innerHTML:t._s(t.formData.yDistance)}})])],1)],1)]),t._v(" "),e("div",{staticClass:"panel panel-primary"},[e("div",{staticClass:"panel-heading",staticStyle:{"text-align":"left"}},[e("h3",{staticClass:"panel-title"},[t._v("保养详情")])]),t._v(" "),e("div",{staticClass:"panel-body",staticStyle:{"margin-left":"-20px"}},[e("el-col",{attrs:{span:6}},[e("el-form-item",{attrs:{label:"保养联系人:"}},[e("span",{domProps:{innerHTML:t._s(t.formData.customerNameInMaintainRecord)}})])],1),t._v(" "),e("el-col",{attrs:{span:6}},[e("el-form-item",{attrs:{label:"联系电话:"}},[e("span",{domProps:{innerHTML:t._s(t.formData.customerPhoneInMaintainRecord)}})])],1),t._v(" "),e("el-col",{attrs:{span:6}},[e("el-form-item",{attrs:{label:"地址:"}},[e("span",{domProps:{innerHTML:t._s(t.formData.customerAddressInMaintainRecord)}})])],1),t._v(" "),e("el-col",{attrs:{span:6}},[e("el-form-item",{attrs:{label:"保养员:"}},[e("span",{domProps:{innerHTML:t._s(t.formData.maintainChargePersonName)}})])],1),t._v(" "),e("el-col",{attrs:{span:6}},[e("el-form-item",{attrs:{label:"保养时间:"}},[e("span",[t._v(t._s(t._f("filterDateString")(t.formData.maintainDateActual)))])])],1)],1)]),t._v(" "),e("el-tabs",{attrs:{type:"border-card"},on:{"tab-click":t.tabSwitchClick},model:{value:t.activeTabId,callback:function(a){t.activeTabId=a},expression:"activeTabId"}},[e("el-tab-pane",{attrs:{label:"保养内容",name:"0"}},[e("el-row",[e("el-col",{attrs:{span:6}},[e("el-form-item",{attrs:{label:"保养项:"}},[e("span",{domProps:{innerHTML:t._s(t.formData.maintainLibName)}})])],1),t._v(" "),e("el-col",{attrs:{span:6}},[e("el-form-item",{attrs:{label:"保养时间:"}},[e("span",[t._v(t._s(t._f("filterDateString")(t.formData.maintainDateActual)))])])],1)],1),t._v(" "),t.maintainCotentList.length>0?e("div",t._l(t.maintainCotentList,function(a){return e("div",{staticClass:"panel panel-primary",staticStyle:{"font-weight":"bold","font-size":"20px"}},[e("div",{staticClass:"panel-heading",staticStyle:{"text-align":"left"}},[e("span",{staticClass:"panel-title"},[t._v(t._s(t._f("filterMaintainTypeName")(a.maintainType)))])]),t._v(" "),e("div",{staticClass:"panel-body"},t._l(a.contentList,function(a){return e("el-col",{attrs:{span:20}},[e("svg-icon",{staticStyle:{width:"30px",height:"30px","margin-left":"3px","margin-top":"5px"},attrs:{"icon-class":t.showCheckBox(a.checkValue)}}),t._v(" "),e("span",{staticStyle:{"font-weight":"bold","font-size":"20px","margin-left":"5px"}},[t._v(t._s(a.content))])],1)}))])})):e("span",[t._v("\n                    暂无内容\n                ")])],1),t._v(" "),t.isShowAgent?e("el-tab-pane",{attrs:{label:"代理商保养",name:"1"}},[e("el-col",{attrs:{span:6}},[e("el-form-item",{attrs:{label:"保养负责人:"}},[e("el-tag",{staticClass:"tagClass",attrs:{type:"success"}},[t._v("\n                                "+t._s(t.formData.maintainChargePersonName)+"\n                        ")])],1)],1),t._v(" "),e("el-col",{attrs:{span:6}},[e("el-form-item",{attrs:{label:"联系电话:"}},[e("span",{domProps:{innerHTML:t._s(t.formData.maintainChargePersonPhone)}})])],1),t._v(" "),e("el-col",{attrs:{span:6}},[e("el-form-item",{attrs:{label:"保养时间:"}},[e("span",[t._v(t._s(t._f("filterDateString")(t.formData.maintainDateActual)))])])],1),t._v(" "),e("el-col",{attrs:{span:6}},[e("el-form-item",{attrs:{label:"计划时间:"}},[e("span",[t._v(t._s(t._f("filterDateString")(t.formData.maintainDatePlan)))])])],1),t._v(" "),e("el-row",[e("el-col",[e("el-form-item",{attrs:{label:"保养人员:"}},t._l(t.maintainMembers,function(a){return e("el-tag",{key:a.name,staticClass:"tagClass",attrs:{type:"info"}},[t._v("\n                                "+t._s(a.name)+"\n                            ")])}))],1)],1),t._v(" "),e("el-col",{attrs:{span:22}},[e("el-form-item",{attrs:{label:"保养建议:"}},[e("span",[t._v(t._s(t.formData.maintainSuggestion))])])],1),t._v(" "),e("el-col",{attrs:{span:6}},[e("el-form-item",{attrs:{label:"保养结果:"}},[0==t.formData.maintainStatus?e("div",{staticClass:"status_class",staticStyle:{color:"#686868"}},[t._v("\n                                "+t._s(t._f("filterStatus")(t.formData.maintainStatus))+"\n                        ")]):t._e(),t._v(" "),1==t.formData.maintainStatus?e("div",{staticClass:"status_class",staticStyle:{color:"#8b6c0e"}},[t._v("\n                                "+t._s(t._f("filterStatus")(t.formData.maintainStatus))+"\n                        ")]):t._e(),t._v(" "),2==t.formData.maintainStatus?e("div",{staticClass:"status_class",staticStyle:{color:"#13678b"}},[t._v("\n                            "+t._s(t._f("filterStatus")(t.formData.maintainStatus))+"\n                        ")]):t._e(),t._v(" "),3==t.formData.maintainStatus?e("div",{staticClass:"status_class",staticStyle:{color:"#198b57"}},[t._v("\n                            "+t._s(t._f("filterStatus")(t.formData.maintainStatus))+"\n                        ")]):t._e(),t._v(" "),4==t.formData.maintainStatus?e("div",{staticClass:"status_class",staticStyle:{color:"darkred"}},[t._v("\n                            "+t._s(t._f("filterStatus")(t.formData.maintainStatus))+"\n                        ")]):t._e()])],1)],1):t._e(),t._v(" "),t.isShowSinsim?e("el-tab-pane",{attrs:{label:"信胜保养",name:"2"}},[e("el-col",{attrs:{span:6}},[e("el-form-item",{attrs:{label:"保养负责人:"}},[e("el-tag",{staticClass:"tagClass",attrs:{type:"success"}},[t._v("\n                                "+t._s(t.formData.maintainChargePersonName)+"\n                        ")])],1)],1),t._v(" "),e("el-col",{attrs:{span:6}},[e("el-form-item",{attrs:{label:"联系电话:"}},[e("span",{domProps:{innerHTML:t._s(t.formData.maintainChargePersonPhone)}})])],1),t._v(" "),e("el-col",{attrs:{span:6}},[e("el-form-item",{attrs:{label:"保养时间:"}},[e("span",[t._v(t._s(t._f("filterDateString")(t.formData.maintainDateActual)))])])],1),t._v(" "),e("el-col",{attrs:{span:6}},[e("el-form-item",{attrs:{label:"计划时间:"}},[e("span",[t._v(t._s(t._f("filterDateString")(t.formData.maintainDatePlan)))])])],1),t._v(" "),e("el-row",[e("el-col",[e("el-form-item",{attrs:{label:"保养人员:"}},t._l(t.maintainMembers,function(a){return e("el-tag",{key:a.name,staticClass:"tagClass",attrs:{type:"info"}},[t._v("\n                                "+t._s(a.name)+"\n                            ")])}))],1)],1),t._v(" "),e("el-col",{attrs:{span:22}},[e("el-form-item",{attrs:{label:"保养建议:"}},[e("span",[t._v(t._s(t.formData.maintainSuggestion))])])],1),t._v(" "),e("el-col",{attrs:{span:6}},[e("el-form-item",{attrs:{label:"保养结果:"}},[0==t.formData.maintainStatus?e("div",{staticClass:"status_class",staticStyle:{color:"#686868"}},[t._v("\n                                "+t._s(t._f("filterStatus")(t.formData.maintainStatus))+"\n                        ")]):t._e(),t._v(" "),1==t.formData.maintainStatus?e("div",{staticClass:"status_class",staticStyle:{color:"#8b6c0e"}},[t._v("\n                                "+t._s(t._f("filterStatus")(t.formData.maintainStatus))+"\n                        ")]):t._e(),t._v(" "),2==t.formData.maintainStatus?e("div",{staticClass:"status_class",staticStyle:{color:"#13678b"}},[t._v("\n                            "+t._s(t._f("filterStatus")(t.formData.maintainStatus))+"\n                        ")]):t._e(),t._v(" "),3==t.formData.maintainStatus?e("div",{staticClass:"status_class",staticStyle:{color:"#198b57"}},[t._v("\n                            "+t._s(t._f("filterStatus")(t.formData.maintainStatus))+"\n                        ")]):t._e(),t._v(" "),4==t.formData.maintainStatus?e("div",{staticClass:"status_class",staticStyle:{color:"darkred"}},[t._v("\n                            "+t._s(t._f("filterStatus")(t.formData.maintainStatus))+"\n                        ")]):t._e()])],1)],1):t._e(),t._v(" "),e("el-tab-pane",{attrs:{label:"客户评价",name:"3"}},[e("el-col",{attrs:{span:22}},[e("el-form-item",{attrs:{label:"用户评分:"}},[e("div",{staticStyle:{"font-size":"20px","font-weight":"bold"}},[t._l(t.skillStars,function(a){return e("div",{staticStyle:{float:"left"}},[e("svg-icon",{staticStyle:{width:"30px",height:"30px",margin:"3px"},attrs:{"icon-class":t.onStarLoad(a),"data-toggle":"tooltip","data-placement":"top",title:a.score}})],1)}),t._v(" "),""!=t.formData.maintainFeedbackCustomerMark?e("div",{staticClass:"control-label",staticStyle:{float:"left","margin-left":"10px","font-weight":"bold"}},[t._v("\n                                        "+t._s(t.formData.maintainFeedbackCustomerMark)+"分\n                            ")]):e("div",{staticClass:"control-label",staticStyle:{float:"left","margin-left":"10px","font-weight":"bold"}},[t._v("\n                                        暂无评分\n                            ")])],2)])],1),t._v(" "),e("el-col",{attrs:{span:22}},[e("el-form-item",{attrs:{label:"用户评论:"}},[e("div",{staticClass:"control-label",staticStyle:{float:"left","margin-left":"10px","font-weight":"bold"}},[t._v("\n                            "+t._s(t.formData.maintainFeedbackCustomerSuggestion)+"\n                        ")])])],1)],1)],1)],1)],1)},staticRenderFns:[]};var p=e("VU/8")(c,f,!1,function(t){e("Lzub")},"data-v-6c530fd4",null);a.default=p.exports},Lzub:function(t,a,e){var n=e("YbnE");"string"==typeof n&&(n=[[t.i,n,""]]),n.locals&&(t.exports=n.locals);e("rjj0")("a8e45438",n,!0)},YbnE:function(t,a,e){(t.exports=e("FZ+f")(!1)).push([t.i,"\n.root_div[data-v-6c530fd4] {\n\tposition: relative;\n\tpadding: 20px;\n\twidth: 100%;\n}\nspan[data-v-6c530fd4] {\n\tfont-size: 18px;\n\twidth: 100%;\n\talignment: left;\n\ttext-align: left;\n}\n.tagClass[data-v-6c530fd4] {\n\tmargin-left: 5px;\n\twidth: 200px;\n}\n.status_class[data-v-6c530fd4] {\n\tfont-size: 18px;\n\tfont-weight: bold;\n}\n",""])}});