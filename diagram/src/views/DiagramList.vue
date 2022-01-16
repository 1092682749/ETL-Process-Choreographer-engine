<template>
  <div>This is list of diagram!
    <div v-for="(item, i) in listOfDiagram" :key="i" @click="goView(item)">
      {{ item.id }}:{{ item.name }}

    </div>
  </div>
</template>

<script>
import {diagramService} from "@/services/diagram-service";

export default {
  name: "DiagramList",
  data() {
    return {listOfDiagram: []}
  },
  created() {
    diagramService.queryList().then((res) => {
      // this.listOfDiagram = JSON.parse(res.data.data);
      let response = res.data.data;
      for (let i = 0; i < response.length; i++) {
        this.listOfDiagram.push(JSON.parse(response[i]))
      }
      console.log(this.listOfDiagram);
    })
  },
  methods: {
    goView(item) {
      this.$router.push({name: 'view', params: {name: item.name, id: item.id}})
    }
  }
}
</script>

<style scoped>

</style>