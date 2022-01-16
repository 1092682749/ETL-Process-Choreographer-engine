/**
 * @author dingyinzhao
 * @data 2022/1/10 19:56
 * @version 1.0
 **/
/**
 * node数据结构
 */
let a = 0
export {a};

import {diagramService} from './diagram-service.js'

class Node {

    constructor() {
        this.nodeId = 'node_' + generateUUID();
        // this.id= '';
        this.code = ''; // 任务详情
        this.name = ''; // 任务名
        this.y = ''; // node的 纵坐标
        this.x = ''; // node 的 横坐标
        this.nodeType = 'gr';
    }
}

/**
 * 线条
 */
class Edge {
    constructor() {
        this.edgeId = 'edge_' + generateUUID();
        this.startNodeId = '';
        this.endNodeId = '';
        this.startNode = Node; // 起始节点
        this.endNode = Node; // 结束节点
    }
}

const app = new Vue({
    el: "#app",
    data: {
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
        editor: null,
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
    },
    mounted: function () {
        this.initCodeMirror();
    },
    computed: {
        draging: function () {
            return this.eventName == 'add_node';
        },
        draging_node: function () {
            return this.eventName == 'move_node';
        }
    },
    methods: {
        initCodeMirror: function () {
            var mime = 'text/x-mariadb';
            this.editor = CodeMirror.fromTextArea(this.$refs.codeEditor, {
                mode: mime,
                indentWithTabs: true,
                smartIndent: true,
                lineNumbers: true,
                matchBrackets: true,
                autofocus: true,
                autoRefresh: true,
                extraKeys: {"Ctrl-Space": "autocomplete"},
                hintOptions: {
                    tables: {
                        users: ["name", "score", "birthDate"],
                        countries: ["name", "population", "size"]
                    }
                },

            });
            setTimeout(() => {
                this.editor.refresh()
            }, 300)
        },
        fullScanner: function () {
            // console.log(123)
            if (!this.bigWindowModel) {
                this.defaultEditorWidth = document.body.clientWidth;
                this.editorModalTop = '10px';
                this.editor.setSize('auto', document.body.clientHeight - 200);
            } else {
                this.defaultEditorWidth = 850;
                this.editor.setSize('auto', 300);
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
                this.moveX = e.pageX - this.anchorX;
                this.moveY = e.pageY - this.anchorY;

            }
            if (this.draging_node) {
                // console.log('X' + e.offsetX);
                this.nodes[this.draging_node_i].x = e.pageX - this.anchorX;
                this.nodes[this.draging_node_i].y = e.pageY - this.anchorY;
            }
            e.stopPropagation();
            e.preventDefault();
        },
        // 松开拖拽节点
        dragEnd: function (e) {
            if (this.draging) {
                this.showDragNode = false;
                this.drawNode();
            }
            if (this.draging_node) {
            }
            this.eventName = '';
        },
        // 在完成拖拽的位置画一个节点
        drawNode: function () {
            const node = new Node();
            node.x = this.moveX;
            node.y = this.moveY;


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
            this.nodes[this.editingNode].code = this.editor.getValue();
            this.nodes[this.editingNode].name = this.editingNodeName;
            this.nodes[this.editingNode].nodeType = this.editingNodeType;
            this.editorModal = false;
            this.$Message.info('保存success');
            this.editor.setValue('');
        },
        // 编辑节点信息
        editNodeInfo: function (i) {
            this.editingNode = i;
            this.editorModal = !this.editorModal;
            this.editor.setValue(this.nodes[i].code);
        },
        //   模拟运行task
        runNodeCode: function (i) {
            this.$Message.success('运行成功!');
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
            })
        },
        // 复原一个流程图
        coverDiagram: function () {
            diagramService.read("myfollow",)
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
            diagramService.run("myfollow");
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
    created: function (e) {

    }
})


function generateUUID() {
    var d = new Date().getTime();
    if (window.performance && typeof window.performance.now === "function") {
        d += performance.now(); //use high-precision timer if available
    }
    var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        var r = (d + Math.random() * 16) % 16 | 0;
        d = Math.floor(d / 16);
        return (c == 'x' ? r : (r & 0x3 | 0x8)).toString(16);
    });
    return uuid;
}