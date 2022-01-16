<template>
  <div id="app-box"
       ref="appBox"
       v-on:mousemove="dragMove"
       v-on:mouseup="dragEnd">

    <svg
        id="svgContent"
        xmlns="http://www.w3.org/2000/svg"
        xmlns:xlink="http://www.w3.org/1999/xlink"
        version="1.1"
        :width="`${svgW}px`"
        :height="`${svgH}px`"

        @mousewheel="onMouseWheelSvg"
    >
      <g
          v-for="(item, i) in nodes"
          :key="'_' + i"
          class="svgEach"
          :transform="`translate(${item.x}, ${item.y}) scale(${svgScale})`"
          @mousedown="dragNodeStart($event, i, item)"
      >
        <foreignObject style="width: 221px;height: 160px">
          <div :class=" `node-box ${i==startNode? 'node-border-start' : ''}`" @click="clickNode($event, i, item)">
                    <span class="node-bar">
                              <a-button type="default" @click="editNodeInfo(i)" title="编辑">📖</a-button>
                                <a-button type="default" title="运行" @click="runNodeCode(i)">🚀</a-button>

                        </span>
            <div class="node-view-window">{{ item.code == undefined ? '' : item.code }}</div>
          </div>
        </foreignObject>
      </g>
      <g v-for="(edge, n) in edges " :key="edge.edgeId">
        <path :class="'connector'"
              v-on:contextmenu="clickEdge($event, n)"
              :d="`M${edge.startNode.x+(nodeW/2)} ${edge.startNode.y+nodeH}
                  Q ${edge.startNode.x+(nodeW/2)} ${(edge.startNode.y+nodeW)} ${(edge.startNode.x+nodeW+edge.endNode.x)/2} ${(edge.startNode.y+nodeH+edge.endNode.y)/2}
                  T ${edge.endNode.x+(nodeW/2)} ${edge.endNode.y}`"
              fill="red" stroke-width="2"/>
        <path
            :d="`M${edge.endNode.x+(nodeW/2)-5} ${edge.endNode.y} h 10 l -5 5 z`" stroke="black"
        ></path>

      </g>
    </svg>

    <div id="tools-bar" ref="bar">
      <div class="node-box" style="width: 100%" v-on:mousedown="dragStart">
        任务框
      </div>
      <div class="node-box" style="width: 100%" v-on:click="startLink">
        <span v-if="!startLinkFlag">开始连线</span>
        <span v-if="startLinkFlag">结束连线</span>
      </div>
      <div class="node-box" style="width: 100%" v-on:click="saveDiagram" v-if="!isEdit">
        保存新流程图
      </div>
      <div class="node-box" style="width: 100%" v-on:click="updateDiagram" v-if="isEdit">
        更新流程图
      </div>
      <!--      <div class="node-box" style="width: 100%" v-on:click="coverDiagram">-->
      <!--        读取流程图-->
      <!--      </div>-->
      <div class="node-box" style="width: 100%" v-on:click="runTask" v-if="isEdit">
        执行
      </div>
      <div class="node-box" style="width: 100%" v-on:click="extendSvg">
        扩展画布
      </div>
    </div>

    <div class="node-box" v-show="showDragNode" ref="mockDragNode" style="z-index: 1000; position: absolute;" id="mock"
         :style="{left: moveX + 'px', top: moveY + 'px' }">
            <span class="node-bar">
                     <a-button type="default">📖</a-button>
                                <a-button type="default">🚀</a-button>
                </span>
    </div>

    <div v-show="showEdgeMenu" class="node-box" ref="edgeMenu" style="position: absolute">
      <a-Button v-on:click="deleteEdge">删除连接线</a-Button>
      <a-Button v-on:click="hideEdgeMenu">什么也不做</a-Button>
    </div>

    <a-modal
        v-model="editorModal"
        title="编辑任务"
        :styles="{top: editorModalTop}"
        :width="defaultEditorWidth">

      任务名：
      <a-Input v-model="editingNodeName">

      </a-Input>

      节点类型：
      <a-Select v-model="editingNodeType" style="width:200px">
        <a-select-option value="start" key="start">开始节点</a-select-option>
        <a-select-option value="gr" key="gr">普通节点</a-select-option>
        <a-select-option value="end" key="end">结束节点</a-select-option>
      </a-Select>
      <codemirror v-model="editingCode" :options="cmOptions" ref="editor"/>

      <div slot="footer">
        <a-Button @click="fullScanner" v-if="!bigWindowModel">大窗编辑模式</a-Button>
        <a-Button @click="fullScanner" v-if="bigWindowModel">小窗编辑模式</a-Button>
        <a-Button @click="saveNodeInfo" type="primary">保存</a-Button>
      </div>
    </a-modal>

  </div>
</template>

<script>
import {diagramService} from "@/services/diagram-service";
import {Node, Edge} from "@/data/ClassFile";
import 'codemirror/lib/codemirror.css'


import 'codemirror/theme/base16-dark.css'
import 'codemirror/mode/sql/sql'

export default {
  name: "DiagramView",

  data() {
    return {
      // draging: false,
      showDragNode: false,
      nodes: [],
      moveX: 0,
      moveY: 0,
      eventName: '',
      draging_node_i: -1,
      nodeW: 150,
      nodeH: 90,
      edges: [],
      startNode: null,
      endNode: null,
      startLinkFlag: false,
      editorModal: false,
      editorModalTop: '100px',
      showEdgeMenu: false,
      selectedEdgeIndex: -1,
      // editor: null,
      defaultEditorWidth: 850,
      bigWindowModel: false,
      anchorX: 0,
      anchorY: 0,
      svgScale: 1,
      editingNode: -1,
      editingNodeName: '',
      editingNodeType: 'gr',
      svgW: document.body.clientWidth,
      svgH: window.screen.height,
      svgTop: 0,
      svgLeft: 0,
      editingCode: '',
      cmOptions: {
        tabSize: 4,
        styleActiveLine: true,
        lineNumbers: true,
        line: true,
        mode: 'text/x-mysql',
        theme: 'base16-dark'
      },

    }
  },
  mounted: function () {
    // this.initCodeMirror();
    // CodeMirror();
  },
  watch: {
    editorModal: function (val) {
      if (true) {
        // this.initCodeMirror();
      }
    }
  },
  computed: {
    draging: function () {
      return this.eventName == 'add_node';
    },
    draging_node: function () {
      return this.eventName == 'move_node';
    },
    codemirror() {
      return this.$refs.editor.codemirror
    },
    isEdit() {
      return this.$route.params.id && this.$route.params.name;
    }
  },
  created() {
    if (this.$route.params.id && this.$route.params.name) {
      this.coverDiagram(this.$route.params.name, this.$route.params.id);
    }

  },
  methods: {
    initCodeMirror: function () {
      // console.log(CodeMirror);
      // var mime = 'text/x-mariadb';
      // this.editor = CodeMirror.fromTextArea(this.$refs.codeEditor, {
      //   mode: mime,
      //   indentWithTabs: true,
      //   smartIndent: true,
      //   lineNumbers: true,
      //   matchBrackets: true,
      //   autofocus: true,
      //   autoRefresh: true,
      //   extraKeys: {"Ctrl-Space": "autocomplete"},
      //   hintOptions: {
      //     tables: {
      //       users: ["name", "score", "birthDate"],
      //       countries: ["name", "population", "size"]
      //     }
      //   },
      //
      // });
      // setTimeout(() => {
      //   this.editor.refresh()
      // }, 300)
    },
    fullScanner: function () {
      // console.log(123)
      if (!this.bigWindowModel) {
        this.defaultEditorWidth = document.body.clientWidth;
        this.editorModalTop = '10px';
        this.codemirror.setSize('auto', document.body.clientHeight - 200);
      } else {
        this.defaultEditorWidth = 850;
        this.codemirror.setSize('auto', 300);
        this.editorModalTop = '100px';
      }
      this.bigWindowModel = !this.bigWindowModel;
    },
    dragStart: function (e) {
      this.eventName = 'add_node';
      this.anchorX = e.offsetX;
      this.anchorY = e.offsetY;
      e.preventDefault();
    },
    dragMove: function (e) {
      if (this.draging) {
        this.showDragNode = true;
        // console.log('X' + e.offsetX);
        this.moveX = e.pageX - this.svgLeft - this.anchorX;
        this.moveY = e.pageY - this.svgTop - this.anchorY;

      }
      if (this.draging_node) {
        // console.log('X' + e.offsetX);
        this.nodes[this.draging_node_i].x = e.pageX - this.svgLeft - this.anchorX;
        this.nodes[this.draging_node_i].y = e.pageY - this.svgTop - this.anchorY;
      }
      e.stopPropagation();
      e.preventDefault();
    },
    // 松开拖拽节点
    dragEnd: function () {
      if (this.draging) {
        this.showDragNode = false;
        this.drawNode();
      }
      // if (this.draging_node) {
      // }
      this.eventName = '';
    },
    // 在完成拖拽的位置画一个节点
    drawNode: function () {
      const node = new Node();
      node.x = this.moveX - this.$refs.appBox.style.left;
      // 减去 header-bar的高度
      node.y = this.moveY - this.$refs.appBox.style.top;


      this.nodes.push(node)
      this.moveX = 0;
      this.moveY = 0;
    },
    // 开始拖拽节点
    dragNodeStart: function (e, i) {
      // console.log(i)
      this.eventName = 'move_node';
      this.draging_node_i = i;
      // console.log('X' + e.offsetX);
      this.anchorX = e.offsetX;
      this.anchorY = e.offsetY;
      e.stopPropagation();
      e.preventDefault();
    },

    // 画线条
    drawLink: function () {
      if (this.startNode !== this.endNode) {
        const edge = new Edge();
        edge.startNodeId = this.nodes[this.startNode].nodeId;
        edge.endNodeId = this.nodes[this.endNode].nodeId;
        edge.startNode = this.nodes[this.startNode];
        edge.endNode = this.nodes[this.endNode];
        this.edges.push(edge);
      }
      this.clearLinkNode();

    }
    ,
    // 开启/关闭 连线开关
    startLink: function (e) {
      this.startLinkFlag = !this.startLinkFlag;
      this.clearLinkNode();
    },
    // 点击节点连线
    clickNode: function (e, i, item) {
      // console.log('点击了:' + item)
      if (this.startLinkFlag) {
        if (this.startNode == null) {
          this.startNode = i;
        } else if (this.endNode == null) {
          this.endNode = i;
          this.drawLink();
        } else {
          this.clearLinkNode();
        }
      }

    },
    // 清空要连接的节点
    clearLinkNode: function () {
      this.startNode = null;
      this.endNode = null;
    },
    // 点击线条（右键） 弹出菜单
    clickEdge: function (e, i) {
      this.selectedEdgeIndex = i;
      this.showEdgeMenu = true;
      let x = e.pageX;
      let y = e.pageY;
      this.$refs.edgeMenu.style.left = x + 'px';
      this.$refs.edgeMenu.style.top = y + 'px';
      e.preventDefault();
    },
    // 删除线条
    deleteEdge: function () {
      this.edges.splice(this.selectedEdgeIndex, 1);
      this.hideEdgeMenu();
    },
    // 隐藏模拟的线条菜单
    hideEdgeMenu: function () {
      this.$refs.edgeMenu.style.top = 0 + 'px';
      this.$refs.edgeMenu.style.left = 0 + 'px';
      this.showEdgeMenu = false;
    },
    // 点击保存节点
    saveNodeInfo: function () {
      // console.log(this.editor.getValue());
      this.nodes[this.editingNode].code = this.editingCode;
      this.nodes[this.editingNode].name = this.editingNodeName;
      this.nodes[this.editingNode].nodeType = this.editingNodeType;
      this.editorModal = false;
      this.$message.info('保存success');
      this.editingCode = '';
    },
    // 编辑节点信息
    editNodeInfo: function (i) {
      this.editingNode = i;
      this.editorModal = !this.editorModal;
      this.editingCode = this.nodes[i].code;
      this.editingNodeType = this.nodes[i].nodeType;
      this.editingNodeName = this.nodes[i].name;
    },
    //   模拟运行task
    runNodeCode: function (i) {
      this.$message.success('运行成功!');
    },
    // 滚动鼠标 缩放svg 目前存在bug 暂不开放
    onMouseWheelSvg: function (e) {
      if (e.wheelDelta > 0) {
        // this.svgScale += 0.1;
      } else {
        // this.svgScale -= 0.1;
      }

    },
    // 保存流程图
    saveDiagram: function (e) {
      // console.log(this.nodes);
      // console.log(this.edges);
      // localStorage.setItem("nodes", JSON.stringify(this.nodes));
      // localStorage.setItem("edges", JSON.stringify(this.edges));

      // let diagramService = new DiagramService();
      diagramService.save({
        name: 'myfollow',
        nodes: this.nodes,
        edges: this.edges
      }).then(res => {
        this.$message.info(res.data.message);
      })
    },
    updateDiagram: function () {
      if (this.$route.params.id && this.$route.params.name) {
        let data = {
          id: this.$route.params.id,
          name: this.$route.params.name,
          nodes: this.nodes,
          edges: this.edges
        }
        diagramService.update(data).then((res) => {

        })
      }
    },
    // 复原一个流程图
    coverDiagram: function (name, id) {

      diagramService.read(name, id)
          .then((response) => {
            this.nodes = response.data.data.nodes;
            let edges = response.data.data.edges;
            console.log(response);
            for (let i = 0; i < edges.length; i++) {
              let startId = edges[i].startNode.nodeId;
              let endId = edges[i].endNode.nodeId;
              edges[i].startNode = this.nodes[this.nodes.map(i => i.nodeId).indexOf(startId)];
              edges[i].endNode = this.nodes[this.nodes.map(i => i.nodeId).indexOf(endId)];
            }
            this.edges = edges;
          });


      //     this.nodes = JSON.parse(localStorage.getItem("nodes"));
      //     let edges = JSON.parse(localStorage.getItem("edges"));
      //     for (let i = 0; i < edges.length; i++) {
      //         let startId = edges[i].startNode.nodeId;
      //         let endId = edges[i].endNode.nodeId;
      //         edges[i].startNode = this.nodes[this.nodes.map(i => i.nodeId).indexOf(startId)];
      //         edges[i].endNode = this.nodes[this.nodes.map(i => i.nodeId).indexOf(endId)];
      //     }
      // this.edges = edges;
    },
    runTask: function () {
      if (this.$route.params.id && this.$route.params.name) {
        diagramService.run(this.$route.params.name, this.$route.params.id);
      } else {
        this.$message.info("请先保存任务");
      }

    },
    extendSvg: function () {
      this.svgW += 500;
      this.svgH += 500;
    },
    queryList: function () {
      diagramService.queryList().then(
          (res) => {

          }
      )
    }
  },
  updated: function (e) {
    this.svgLeft = getLeft(this.$refs.appBox);
    this.svgTop = getTop(this.$refs.appBox);
  }

}

//获取元素的纵坐标
function getTop(e) {
  var offset = e.offsetTop;
  if (e.offsetParent != null) offset += getTop(e.offsetParent);
  return offset;
}

//获取元素的横坐标
function getLeft(e) {
  var offset = e.offsetLeft;
  if (e.offsetParent != null) offset += getLeft(e.offsetParent);
  return offset;
}

</script>

<style scoped>
* {
  margin: 0;
}

#app-box {
  position: relative;
}

/** 给svg添加格子背景 */
#svgContent {
  background-size: 50px 50px;
  background-image: linear-gradient(
      0deg,
      transparent 24%,
      rgba(255, 255, 255, 0.05) 25%,
      rgba(255, 255, 255, 0.05) 26%,
      transparent 27%,
      transparent 74%,
      rgba(255, 255, 255, 0.05) 75%,
      rgba(255, 255, 255, 0.05) 76%,
      transparent 77%,
      transparent
  ),
  linear-gradient(
      90deg,
      transparent 24%,
      rgba(255, 255, 255, 0.05) 25%,
      rgba(255, 255, 255, 0.05) 26%,
      transparent 27%,
      transparent 74%,
      rgba(255, 255, 255, 0.05) 75%,
      rgba(255, 255, 255, 0.05) 76%,
      transparent 77%,
      transparent
  );
  background-color: rgb(60, 60, 60) !important;
  /*margin-left: 200px;*/
  /*margin-top: 40px;*/
  /*margin-right: 200px;*/
}

#tools-bar {
  width: 100px;
  height: 100vh;
  z-index: 1000;
  background: #f1efef;
  position: fixed;
  top: 80px;
  /*left: 0;*/
  /*bottom: 0;*/
}

.node-box {
  width: 150px;
  min-height: 90px;
  border: solid 5px #dfe6e8;
  border-radius: 6px;
  background: #f4f5ef;
}

.node-bar {
  display: inline-block;
  width: 100%;
  height: 50px;
  line-height: 50px;
  /*text-align: left;*/
  background: #fafcfd;
  margin: -1px;
  padding: 1px;
  text-align: center;
}

.node-border-start {
  /*border-color: #39ef39;*/
  box-shadow: 0 4px 8px 0 rgba(255, 0, 0, 0.5), 0 6px 20px 0 rgba(255, 0, 0, 0.3);
}

.node-btn {

}

.connector {
  stroke: #00c0ff;
  stroke-width: 2px;
  /* fill: none; */
  fill: transparent;
  cursor: pointer;
  stroke-dasharray: 100%;
  animation: line_success 0.5s;
}

@keyframes line_success {
  0% {
    stroke-dashoffset: 100%;
  }
  100% {
    stroke-dashoffset: 0%;
  }
}

.defaultArrow {
  stroke: #00c0ff;
  stroke-width: 2px;
  /* fill: none; */
  fill: transparent;
  cursor: pointer;
  stroke-dasharray: 5px;
  stroke-dashoffset: 1000px;
  animation: grown 40s infinite linear;
}

@keyframes grown {
  to {
    stroke-dashoffset: 0px;
  }
}

.CodeMirror {

  font-size: 16px;
}

.node-view-window {
  height: 100%;
  width: 100%;
  overflow: hidden;
}
</style>