<script setup lang="ts">
import {ref} from "vue";
import {Toast, VButton, VPageHeader} from "@halo-dev/components";

const isLoading = ref(false);

function updateData() {
  isLoading.value = true;
  fetch("/apis/io.sakurasou.halo.bangumi/v1/userData", {
    method: "PUT"
  }).then(response => response.json())
          .then(data => {
            Toast({
              content: data.msg,
              type: data.success ? "success" : "error"
            })
  }).catch(err => {
    Toast({
      content: "更新数据失败",
      type: "error"
    });
    console.error("更新数据失败", err);
  }).finally(() => {
    isLoading.value = false;
  });
}
</script>

<template>
  <div id="container" class="w-full h-full">
    <VPageHeader title="BangumiData"></VPageHeader>
    <div class="m-4 p-3 bg-white rounded flex flex-col">
      <div class="flex flex-row gap-4">
        <span>手动更新数据</span>
        <span>
            <VButton
                    @click="updateData"
                    :loading="isLoading"
                    v-permission="['plugin:bangumi-data:update']"
                    size="sm"
            >
              更新
            </VButton>
        </span>
      </div>
    </div>
  </div>
</template>
