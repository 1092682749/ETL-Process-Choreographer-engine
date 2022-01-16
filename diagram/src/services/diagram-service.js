// import axios from './axios/axios.js'
// import axios from "./axios/axios.js";
// const axios = require("./axios/axios.js");
import axios from "axios";
import ROOT_URL from '../assets/config.js'


class DiagramService {
    save(data) {
        return axios.post(`${ROOT_URL}/v2/task/diagram/saveDiagram`, data);
    };

    update(data) {
        return axios.post(`${ROOT_URL}/v2/task/diagram/updateDiagram`, data);
    };
    read(name, id, success, failed) {
        return axios.post(`${ROOT_URL}/v2/task/diagram/readDiagram`, {id, name});
    };
    run(name, id) {
        return axios.post(`${ROOT_URL}/v2/task/diagram/runTask`, {id, name});
    };
    queryList() {
        return axios.get(`${ROOT_URL}/v2/task/diagram/queryList`);
    }
}


const diagramService = new DiagramService();

export {diagramService};


