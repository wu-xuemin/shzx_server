webpackJsonp([20],{Ift6:function(t,e,n){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var a,o=n("BO1k"),i=n.n(o),l=n("D2/k"),s=(n("o1Jv"),n("zL8q"),n("vt++")),r=n("0xDb"),d={name:"agent_manage",components:{},data:function(){return a=this,{loadingUI:!1,totalRecords:0,totalPage:1,pageSize:s.a.EveryPageNum,currentPage:1,startRow:1,tableData:[],selectedItem:[],isAdd:!0,addDialogVisible:!1,condition:{name:""},addContentform:{name:"",address:"",phone:""},contentformTitle:"代理商",deleteContentConfirmDialog:!1}},methods:{onConfirmDeleteContent:function(){var t=this;Object(l.c)(this.selectedItem.id).then(function(e){if(responseIsOK(e)){var n=!0,o=!1,l=void 0;try{for(var s,r=i()(a.tableData);!(n=(s=r.next()).done);n=!0){var d=s.value;if(d.id==a.selectedItem.id){var c=a.tableData.indexOf(d);a.tableData.splice(c,1)}}}catch(t){o=!0,l=t}finally{try{!n&&r.return&&r.return()}finally{if(o)throw l}}t.deleteContentConfirmDialog=!1}else showMSG(a,isStringEmpty(e.data.message)?"删除失败！":e.data.message)})},handleCurrentChange:function(t){this.currentPage=t,this.initData()},handleAddContent:function(){Object(r.b)(a.addContentform),this.isAdd=!0,this.addDialogVisible=!0,this.contentformTitle=this.isAdd?"增加代理商":"编辑代理商"},addContentOK:function(){isStringEmpty(this.addContentform.name)?showMessage(this,"代理商不能为空！"):isStringEmpty(this.addContentform.address)?showMessage(this,"地址不能为空！"):isStringEmpty(this.addContentform.phone)?showMessage(this,"电话不能为空！"):this.isAdd?Object(l.a)(this.addContentform).then(function(t){responseIsOK(t)?(a.onSearchDetailData(),a.addDialogVisible=!1,showMSG(a,"保存成功！",1)):showMSG(a,isStringEmpty(t.data.message)?"保存失败！":t.data.message)}):Object(l.g)(this.addContentform).then(function(t){if(responseIsOK(t)){var e=!0,n=!1,o=void 0;try{for(var l,s=i()(a.tableData);!(e=(l=s.next()).done);e=!0){var d=l.value;d.id==a.addContentform.id&&(d.name=a.addContentform.name,d.address=a.addContentform.address,d.phone=a.addContentform.phone)}}catch(t){n=!0,o=t}finally{try{!e&&s.return&&s.return()}finally{if(n)throw o}}Object(r.b)(a.addContentform),a.addDialogVisible=!1,showMSG(a,"修改成功！",1)}else showMSG(a,isStringEmpty(t.data.message)?"提交失败！":t.data.message)})},handleTabClick:function(t,e){a.onSearchDetailData()},handleEdit:function(t){this.isAdd=!1,this.addContentform=copyObjectByJSON(t),this.addDialogVisible=!0,this.contentformTitle=this.isAdd?"增加代理商":"编辑代理商"},handleDelete:function(t){this.selectedItem=copyObjectByJSON(t),this.deleteContentConfirmDialog=!0},onSearchDetailData:function(){Object(r.b)(a.addContentform);var t={page:this.currentPage,size:this.pageSize,name:this.condition.name,isFuzzy:!0};Object(l.d)(t).then(function(t){responseIsOK(t)?(a.totalRecords=t.data.data.total,a.startRow=t.data.data.startRow,a.tableData=t.data.data.list):showMSG(a,isStringEmpty(t.data.message)?"查询数据失败！":t.data.message)})},search:function(){a.onSearchDetailData()}},created:function(){},mounted:function(){this.search()}},c={render:function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("div",{staticClass:"root_div"},[n("el-form",{attrs:{model:t.condition,"label-position":"right","label-width":"60px"}},[n("el-row",[n("el-col",{attrs:{span:4}},[n("el-form-item",{attrs:{label:"代理商:"}},[n("el-input",{attrs:{placeholder:"代理商名称","auto-complete":"off",clearable:""},model:{value:t.condition.name,callback:function(e){t.$set(t.condition,"name",e)},expression:"condition.name"}})],1)],1),t._v(" "),n("el-col",{attrs:{span:2,offset:18}},[n("el-button",{attrs:{icon:"el-icon-search",size:"normal",type:"primary"},on:{click:t.search}},[t._v("搜索\n                    ")])],1)],1)],1),t._v(" "),n("div",{staticStyle:{"margin-bottom":"16px"},attrs:{align:"right"}},[n("el-tooltip",{attrs:{placement:"right"}},[n("div",{attrs:{slot:"content"},slot:"content"},[t._v("增加代理商")]),t._v(" "),n("el-button",{attrs:{icon:"el-icon-plus",size:"small",type:"primary"},on:{click:t.handleAddContent}})],1)],1),t._v(" "),n("el-table",{directives:[{name:"loading",rawName:"v-loading",value:t.loadingUI,expression:"loadingUI"}],staticStyle:{width:"100%","font-size":"16px"},attrs:{"element-loading-text":"获取数据中...",data:t.tableData,border:"","highlight-current-row":"","empty-text":"暂无数据...","show-overflow-tooltip":"true"}},[n("el-table-column",{attrs:{width:"75",align:"center",label:"序号"},scopedSlots:t._u([{key:"default",fn:function(e){return[t._v("\n                    "+t._s(e.$index+t.startRow)+"\n                ")]}}])}),t._v(" "),n("el-table-column",{attrs:{align:"center",label:"代理商"},scopedSlots:t._u([{key:"default",fn:function(e){return[n("div",[t._v("\n                        "+t._s(e.row.name)+"\n                    ")])]}}])}),t._v(" "),n("el-table-column",{attrs:{align:"center",label:"地址"},scopedSlots:t._u([{key:"default",fn:function(e){return[n("div",[t._v("\n                        "+t._s(e.row.address)+"\n                    ")])]}}])}),t._v(" "),n("el-table-column",{attrs:{align:"center",label:"电话"},scopedSlots:t._u([{key:"default",fn:function(e){return[n("div",[t._v("\n                        "+t._s(e.row.phone)+"\n                    ")])]}}])}),t._v(" "),n("el-table-column",{attrs:{label:"操作",width:"150",align:"center"},scopedSlots:t._u([{key:"default",fn:function(e){return[n("el-tooltip",{attrs:{placement:"top"}},[n("div",{attrs:{slot:"content"},slot:"content"},[t._v("编辑")]),t._v(" "),n("el-button",{attrs:{size:"small",icon:"el-icon-edit",type:"primary"},on:{click:function(n){t.handleEdit(e.row)}}})],1)]}}])})],1),t._v(" "),n("div",{staticClass:"block",staticStyle:{"text-align":"center","margin-top":"20px"}},[n("el-pagination",{attrs:{background:"","current-page":t.currentPage,"page-size":t.pageSize,layout:"total, prev, pager, next, jumper",total:t.totalRecords},on:{"current-change":t.handleCurrentChange}})],1),t._v(" "),n("el-dialog",{attrs:{title:t.contentformTitle,visible:t.addDialogVisible,width:"40%"},on:{"update:visible":function(e){t.addDialogVisible=e}}},[n("el-form",{attrs:{model:t.addContentform,"label-width":"90px"}},[n("el-col",{attrs:{span:24}},[n("el-form-item",{staticStyle:{width:"100%"},attrs:{label:"代理商："}},[n("el-input",{staticStyle:{"font-size":"18px"},attrs:{clearable:""},model:{value:t.addContentform.name,callback:function(e){t.$set(t.addContentform,"name",e)},expression:"addContentform.name"}})],1)],1),t._v(" "),n("el-col",{attrs:{span:24}},[n("el-form-item",{staticStyle:{width:"100%"},attrs:{label:"地址："}},[n("el-input",{staticStyle:{"font-size":"18px"},attrs:{type:"textarea",rows:5,clearable:""},model:{value:t.addContentform.address,callback:function(e){t.$set(t.addContentform,"address",e)},expression:"addContentform.address"}})],1)],1),t._v(" "),n("el-col",{attrs:{span:24}},[n("el-form-item",{staticStyle:{width:"100%"},attrs:{label:"电话："}},[n("el-input",{staticStyle:{"font-size":"18px"},attrs:{clearable:""},model:{value:t.addContentform.phone,callback:function(e){t.$set(t.addContentform,"phone",e)},expression:"addContentform.phone"}})],1)],1)],1),t._v(" "),n("div",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[n("el-button",{attrs:{size:"small"},on:{click:function(e){t.addDialogVisible=!1}}},[t._v("取 消")]),t._v(" "),n("el-button",{attrs:{type:"primary",size:"small"},on:{click:t.addContentOK}},[t._v("确 定")])],1)],1),t._v(" "),n("el-dialog",{attrs:{title:"提示",visible:t.deleteContentConfirmDialog,"append-to-body":"",width:"30%"},on:{"update:visible":function(e){t.deleteContentConfirmDialog=e}}},[n("span",[t._v("确认要删除选定的代理商吗？")]),t._v(" "),n("span",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[n("el-button",{attrs:{icon:"el-icon-close"},on:{click:function(e){t.deleteContentConfirmDialog=!1}}},[t._v("取 消")]),t._v(" "),n("el-button",{attrs:{type:"primary",icon:"el-icon-check"},on:{click:t.onConfirmDeleteContent}},[t._v("确 定")])],1)])],1)},staticRenderFns:[]};var f=n("VU/8")(d,c,!1,function(t){n("eTPH")},"data-v-7dbd53f2",null);e.default=f.exports},eTPH:function(t,e,n){var a=n("g2Dj");"string"==typeof a&&(a=[[t.i,a,""]]),a.locals&&(t.exports=a.locals);n("rjj0")("1a76f914",a,!0)},g2Dj:function(t,e,n){(t.exports=n("FZ+f")(!1)).push([t.i,"\n.root_div[data-v-7dbd53f2] {\n\tposition: relative;\n\tpadding: 20px;\n\twidth: 100%;\n\theight: 85vh;\n}\n.el-select[data-v-7dbd53f2] {\n\twidth: 100%;\n}\n",""])}});