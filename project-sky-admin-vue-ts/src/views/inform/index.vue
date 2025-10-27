<template>
  <div class="dashboard-container">
    <div class="informBox">
      <ul class="conTab">
        <li
          v-for="(item, index) in tabList"
          :key="index"
          :class="activeIndex === index ? 'active' : ''"
          @click="handleClass(index)"
        >
          <el-badge
            class="item"
            :class="ountUnread >= 10 ? 'badgeW' : ''"
            :value="
              ountUnread === 0 ? null : ountUnread > 99 ? '99+' : ountUnread
            "
            :hidden="!([1].includes(item.value) && ountUnread)"
          >
            {{ item.label }}
          </el-badge>
        </li>
      </ul>

      <el-button
        v-if="status === 1 && baseData.length > 0"
        icon="iconfont icon-clear"
        class="right-el-button"
        @click="handleBatch"
      >
        鍏ㄩ儴宸茶
      </el-button>
      <el-button
        v-else
        icon="iconfont icon-clear"
        class="right-el-button onbutton"
        disabled
      >
        鍏ㄩ儴宸茶
      </el-button>
    </div>
    <div class="container newBox" :class="{ hContainer: baseData.length }">
      <div v-if="baseData.length > 0" class="informList">
        <div v-for="(item, index) in baseData" :key="index">
          <!-- 寰呮帴鍗?-->
          <div v-if="item.type === 1" class="item">
            <div class="tit">
              <span>銆愬緟鎺ュ崟銆?/span>{{ item.arrNew[0]
              }}<span class="fontOrderTip" @click="handleSetStatus(item.id)">
                <router-link :to="'/order?status=' + 2">{{
                  item.arrNew[1]
                }}</router-link></span>{{ item.arrNew[2]
              }}<span class="time">{{ item.createTime }}</span>
              </span>
            </div>
          </div>
          <div v-if="item.type === 2" class="item">
            <div class="tit">
              <i>鎬?/i><span>銆愬緟鎺ュ崟銆?/span>{{ item.arrNew[0]
              }}<span class="fontOrderTip" @click="handleSetStatus(item.id)"><router-link :to="'/order?status=' + 2">{{
                item.arrNew[1]
              }}</router-link></span>{{ item.arrNew[2]
              }}<span class="time">{{ item.createTime }}</span>
              </span></i>
            </div>
          </div>
          <!-- end -->
          <!-- 寰呮淳閫?-->
          <div v-if="item.type === 3" class="item">
            <div class="tit">
              <span>銆愬緟娲鹃€併€?/span>{{ item.arrNew[0]
              }}<span class="fontOrderTip" @click="handleSetStatus(item.id)"><router-link :to="'/order?status=' + 2">{{
                item.arrNew[1]
              }}</router-link></span>{{ item.arrNew[2]
              }}<span class="time">{{ item.createTime }}</span>
              </span>
            </div>
          </div>
          <!-- end -->
          <!-- 鍌崟 -->
          <div
            v-if="item.type === 4"
            class="item"
            @mouseenter="toggleShow(item.id, index)"
            @mouseleave="mouseLeaves(index)"
          >
            <div :class="isActive ? 'titAlready' : ''">
              <div class="tit">
                <span>銆愬偓鍗曘€?/span>{{ item.arrNew[0] }}
                  <!-- <span
                  class="fontOrderTip"
                  >鍘诲鐞?/span
                > -->
                  <span class="time">{{ item.createTime }}</span>
                </span>
              </div>
              <div v-if="shopShow && showIndex === index" class="orderInfo">
                <p>
                  <span><label>涓嬪崟鏃堕棿锛?/label>{{ item.details.orderTime }}</label></span><span><label>棰勮閫佽揪鏃堕棿锛?/label>{{ item.details.estimatedDeliveryTime }}</label></span>
                </p>
                <p>
                  {{ item.details.consignee }}锛寋{ item.details.phone }}锛寋{
                  item.details.address
                  }}
                </p>
                <p>
                  <span><label>鑿滃搧锛?/label>{{ item.details.orderDishes }}</label></span>
                </p>
              </div>
            </div>
          </div>
          <!-- end -->
          <!-- <div class="item" v-if="item.type === 4 && isActive && status === 1">
            <div class="titAlready">
              <div class="tit">
                <span>銆愬偓鍗曘€?/span>{{ item.arrNew[0] }}
                <span class="time">{{ item.createTime }}</span>
              </div>
            </div>
          </div> -->
          <!-- 闂簵 -->
          <!-- <div class="item" v-if="item.type === 5 && isActive && status === 1">
            <div class="titAlready">
              <div class="tit">
                <span>銆愪粖鏃ユ暟鎹€?/span>璁ょ湡宸ヤ綔鐨勫悓鏃朵篃瑕佸ソ濂界敓娲汇€?span
                  class="time"
                  >{{ item.createTime }}</span
                >
              </div>
            </div>
          </div> -->
          <div
            v-if="item.type === 5"
            class="item"
            @mouseenter="toggleShow(item.id, index)"
            @mouseleave="mouseLeaves(index)"
          >
            <div :class="isActive ? 'titAlready' : ''">
              <div class="tit">
                <span>銆愪粖鏃ユ暟鎹€?/span>璁ょ湡宸ヤ綔鐨勫悓鏃朵篃瑕佸ソ濂界敓娲汇€?span
                  class="time"
                  >{{ item.createTime }}</span>
              </div>
              <div v-if="shopShow && showIndex === index" class="orderInfo">
                <p>
                  <span><label>钀ヤ笟棰濓細</label>{{ item.details.turnover }}</span>
                  <span><label>鏈夋晥璁㈠崟锛?/label>{{ item.details.validOrderCount }}绗?/span>
                    <span><label>璁㈠崟瀹屾垚鐜囷細</label>{{ item.details.orderCompletionRate }}</span>
                  </label></span>
                </p>
                <p>
                  <span><label>浠婃棩鏂板鐢ㄦ埛锛?/label>{{ item.details.newUsers }}</label></span>
                  <span><label>浠婃棩鍙栨秷锛?/label>{{ item.details.cancelledOrders }}绗?/span>
                    <span><label>浠婃棩鍙栨秷閲戦锛?/label>锟{
                      item.details.cancelledAmount
                      }}</label></span>
                  </label></span>
                </p>
              </div>
            </div>
          </div>
        </div>
        <!-- end -->
      </div>
      <Empty v-else :is-search="isSearch" />
      <el-pagination
        v-if="counts > 10"
        class="pageList"
        :page-sizes="[10, 20, 30, 40]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="counts"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Inject } from 'vue-property-decorator'
import Empty from '@/components/Empty/index.vue'
import { getNewData, setNewData } from '@/utils/cookies'
import { AppModule } from '@/store/modules/app'
// 鎺ュ彛
import {
  getInformData,
  batchMsg,
  setStatus,
  getCountUnread,
} from '@/api/inform'
@Component({
  name: 'Inform',
  components: {
    Empty,
  },
})
export default class extends Vue {
  // @Inject('reload') readonly reload!: Function
  private activeIndex = 0
  private shopShow = false
  private counts: number = 0
  private page: number = 1
  private pageSize: number = 10
  private status = 1
  private baseData = []
  // private ountUnread = 0 as any
  private showIndex = 0
  private isSearch: boolean = false
  private isActive: boolean = false

  get tabList() {
    return [
      {
        label: '鏈',
        value: 1,
        // num: this.ountUnread,
      },
      {
        label: '宸茶',
        value: 2,
        // num: 0,
      },
    ]
  }
  get ountUnread() {
    return AppModule.statusNumber
  }
  created() {
    this.getData()
  }
  // 鑾峰彇鍒楄〃鏁版嵁
  async getData() {
    const parent = {
      pageNum: this.page,
      pageSize: this.pageSize,
      status: this.status,
    }
    const { data } = await getInformData(parent)
    if (data.code === 1) {
      this.baseData = data.data.records
      this.counts = data.data.total
      let objNew = {} as any
      let arrDetails = []
      this.baseData.forEach((val) => {
        // 澶勭悊鍚庣杩斿洖鐨勭姸璁㈠崟瀛楃涓茶浆涔?
        const arrContent = val.content.split(' ')
        // 澶勭悊鍌崟銆侀棴搴楄鎯呮暟鎹?
        val.arrNew = arrContent
        objNew = { ...val }
        objNew.details = this.safeParseDetails(objNew.details)
        arrDetails.push(objNew)
      })

      this.baseData = arrDetails
      // this.$message.success('鎿嶄綔鎴愬姛锛?)
    } else {
      this.$message.error(data.msg)
    }
  }

  // 鍏ㄩ儴宸茶
  async handleBatch() {
    const ids = []
    this.baseData.forEach((val) => {
      ids.push(val.id)
    })
    const { data } = await batchMsg(ids)
    if (data.code === 1) {
      // this.status = 2
      // this.activeIndex = 1
      this.getCountUnread()
      this.getData()
      // this.$message.success('鎿嶄綔鎴愬姛锛?)
    } else {
      this.$message.error(data.msg)
    }
  }
  // 璁剧疆鍗曚釜璁㈠崟宸茶
  async handleSetStatus(id) {
    const { data } = await setStatus(id)
    if (data.code === 1) {
      // this.status = 2
      // this.activeIndex = 1
      if (!this.isActive) {
        this.getCountUnread()
        this.getData()
      }

      // this.reload()
      // this.$message.success('鎿嶄綔鎴愬姛锛?)
    } else {
      this.$message.error(data.msg)
    }
  }
  // 鑾峰彇鏈娑堟伅
  async getCountUnread() {
    const { data } = await getCountUnread()
    if (data.code === 1) {
      AppModule.StatusNumber(data.data)
      // this.$message.success('鎿嶄綔鎴愬姛锛?)
    } else {
      this.$message.error(data.msg)
    }
  }
  private safeParseDetails(payload: any): Record<string, any> {
    if (!payload) {
      return {}
    }
    if (typeof payload === 'object') {
      return payload
    }
    const raw = String(payload).trim()
    const candidates = [raw, raw.replace(/'/g, '"')]
    for (const candidate of candidates) {
      if (!candidate) continue
      try {
        return JSON.parse(candidate)
      } catch (error) {
        continue
      }
    }
    console.warn('[inform] Unable to parse details payload', payload)
    return {}
  }

  // 瑙﹀彂宸茶鏈鎸夐挳
  handleClass(index) {
    this.activeIndex = index
    if (index === 0) {
      this.status = 1
    } else {
      this.status = 2
    }
    this.getData()
  }
  // 涓嬫媺鑿滃崟鏄剧ず
  toggleShow(id, index) {
    this.shopShow = true
    this.showIndex = index
    let t = 3
    let timer = setInterval(() => {
      t--
      if (t === 0) {
        if (this.status === 1) {
          this.isActive = true
          this.handleSetStatus(id)
        }

        clearInterval(timer)
      }
    }, 1000)
  }
  // 涓嬫媺鑿滃崟闅愯棌
  mouseLeaves(index) {
    this.shopShow = false
    this.showIndex = index
  }
  private handleSizeChange(val: any) {
    this.pageSize = val
    this.getData()
  }

  private handleCurrentChange(val: any) {
    this.page = val
    this.getData()
  }
}
</script>

<style lang="scss" scoped>
.dashboard {
  &-container {
    margin: 30px;
    .container {
      background: #fff;
      position: relative;
      z-index: 1;
      padding: 0 30px;
      border-radius: 4px;
      // min-height: 650px;
      height: calc(100% - 55px);
      overflow: hidden;
      &.newBox {
        // padding:0;
        .pageList {
          border-top: 1px solid #f3f4f7;
          padding: 40px;
          margin-top: 0;
        }
      }
    }
    .hContainer {
      height: auto !important;
    }
  }
}
</style>
