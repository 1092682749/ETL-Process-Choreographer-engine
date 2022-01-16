import Vue from 'vue'
import App from './App.vue'
import router from './router'
import axios from 'axios'
import 'ant-design-vue/dist/antd.css';
import 'codemirror/lib/codemirror.css'
import Antd from 'ant-design-vue';
import VueCodemirror from 'vue-codemirror'

Vue.use(VueCodemirror)
Vue.prototype.$axios = axios
Vue.use(Antd);
Vue.config.productionTip = false

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')



