import {definePlugin} from '@halo-dev/console-shared'
import {IconPlug, VLoading} from '@halo-dev/components'
import {defineAsyncComponent, markRaw} from 'vue'

export default definePlugin({
  components: {},
  routes: [
    {
      parentName: 'ToolsRoot',
      route: {
        path: 'bangumi-data',
        name: 'BangumiData',
        component: defineAsyncComponent({
          loader: () => import("@/views/HomeView.vue"),
          loadingComponent: VLoading,
        }),
        meta: {
          title: 'Bangumi 数据源',
          searchable: true,
          permissions: ["plugin:bangumi-data:update"],
          menu: {
            name: 'Bangumi 数据源',
            icon: markRaw(IconPlug),
            priority: 0,
          },
        },
      },
    },
  ],
  extensionPoints: {},
})
