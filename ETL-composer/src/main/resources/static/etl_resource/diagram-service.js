// import axios from './axios/axios.js'
// import axios from "./axios/axios.js";
// const axios = require("./axios/axios.js");
import ROOT_URL from './config.js'
if (!window.axios) {
    window.axios = {};
    throw new Error("依赖axios未找到");
}
class DiagramService {
    save(data) {
        axios.post(`${ROOT_URL}/v2/task/diagram/saveDiagram`, data).then((response) => {
            console.log(response);
        }).catch((response) => {
            console.error(response);
        })
    };
    read(name, success, failed) {
        return axios.post(`${ROOT_URL}/v2/task/diagram/readDiagram`, {id :6, name});
    };
    run(name) {
        return axios.post(`${ROOT_URL}/v2/task/diagram/runTask`, {id :6, name});
    };
    queryList() {
        return axios.get(`${ROOT_URL}/v2/task/diagram/queryList`);
    }
}


const diagramService = new DiagramService();

export {diagramService};


