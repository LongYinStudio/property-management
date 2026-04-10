<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <span>车位管理</span>
      </template>
      <el-tabs v-model="activeTab">
        <!-- 车位列表 -->
        <el-tab-pane label="车位列表" name="space">
          <el-form :inline="true" :model="spaceQueryParams" class="search-form">
            <el-form-item label="状态">
              <el-select
                v-model="spaceQueryParams.status"
                placeholder="请选择状态"
                clearable
                style="width: 120px"
              >
                <el-option label="空闲" :value="0" />
                <el-option label="已出租" :value="1" />
                <el-option label="已出售" :value="2" />
              </el-select>
            </el-form-item>
            <el-form-item label="类型">
              <el-select
                v-model="spaceQueryParams.type"
                placeholder="请选择类型"
                clearable
                style="width: 120px"
              >
                <el-option label="普通车位" :value="1" />
                <el-option label="VIP车位" :value="2" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSpaceSearch">搜索</el-button>
              <el-button @click="handleSpaceReset">重置</el-button>
              <el-button
                type="success"
                @click="handleAddSpace"
                v-if="isAdmin"
                >新增车位</el-button
              >
            </el-form-item>
          </el-form>
          <el-table :data="spaceTableData" v-loading="spaceLoading" style="width: 100%">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="spaceNumber" label="车位编号" width="120" />
            <el-table-column prop="location" label="位置" />
            <el-table-column prop="type" label="类型" width="100">
              <template #default="{ row }">
                <el-tag :type="row.type === 2 ? 'warning' : 'info'">
                  {{ row.type === 2 ? 'VIP' : '普通' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="rentPrice" label="月租价格" width="120">
              <template #default="{ row }">
                <span style="color: #f56c6c">¥{{ row.rentPrice }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">
                  {{ getStatusName(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="ownerName" label="业主" width="100" />
            <el-table-column label="操作" width="280" fixed="right">
              <template #default="{ row }">
                <el-button
                  type="success"
                  link
                  @click="handleAssignSpace(row)"
                  v-if="isAdmin && row.status === 0"
                  >分配</el-button
                >
                <el-button
                  type="primary"
                  link
                  @click="handleRentSpace(row)"
                  v-if="row.status === 0"
                  >租用</el-button
                >
                <el-button
                  type="warning"
                  link
                  @click="handleEditSpace(row)"
                  v-if="isAdmin"
                  >编辑</el-button
                >
                <el-button
                  type="danger"
                  link
                  @click="handleDeleteSpace(row)"
                  v-if="isAdmin && row.status === 0"
                  >删除</el-button
                >
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            v-model:current-page="spaceQueryParams.pageNum"
            v-model:page-size="spaceQueryParams.pageSize"
            :total="spaceTotal"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            class="mt-20"
            @size-change="fetchSpaceData"
            @current-change="fetchSpaceData"
          />
        </el-tab-pane>

        <!-- 租用记录 -->
        <el-tab-pane label="租用记录" name="rental">
          <el-form :inline="true" :model="rentalQueryParams" class="search-form">
            <el-form-item label="状态">
              <el-select
                v-model="rentalQueryParams.status"
                placeholder="请选择状态"
                clearable
                style="width: 120px"
              >
                <el-option label="待支付" :value="0" />
                <el-option label="已支付" :value="1" />
                <el-option label="已过期" :value="2" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleRentalSearch">搜索</el-button>
              <el-button @click="handleRentalReset">重置</el-button>
            </el-form-item>
          </el-form>
          <el-table :data="rentalTableData" v-loading="rentalLoading" style="width: 100%">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="spaceNumber" label="车位编号" width="120" />
            <el-table-column prop="userName" label="用户" v-if="isAdmin" width="100" />
            <el-table-column prop="startDate" label="开始日期" width="120" />
            <el-table-column prop="endDate" label="结束日期" width="120" />
            <el-table-column prop="amount" label="金额" width="120">
              <template #default="{ row }">
                <span style="color: #f56c6c">¥{{ row.amount }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getRentalStatusType(row.status)">
                  {{ getRentalStatusName(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="180" />
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <el-button
                  type="success"
                  link
                  @click="handlePayRental(row)"
                  v-if="row.status === 0"
                  >支付</el-button
                >
                <el-button
                  type="warning"
                  link
                  @click="handleRenewSpace(row)"
                  v-if="row.status === 1"
                  >续费</el-button
                >
                <el-button
                  type="danger"
                  link
                  @click="handleCancelRental(row)"
                  v-if="row.status === 0"
                  >取消</el-button
                >
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            v-model:current-page="rentalQueryParams.pageNum"
            v-model:page-size="rentalQueryParams.pageSize"
            :total="rentalTotal"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            class="mt-20"
            @size-change="fetchRentalData"
            @current-change="fetchRentalData"
          />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 新增/编辑车位对话框 -->
    <el-dialog v-model="spaceDialogVisible" :title="spaceDialogTitle" width="500px">
      <el-form
        ref="spaceFormRef"
        :model="spaceFormData"
        :rules="spaceFormRules"
        label-width="80px"
      >
        <el-form-item label="车位编号" prop="spaceNumber">
          <el-input v-model="spaceFormData.spaceNumber" placeholder="请输入车位编号" />
        </el-form-item>
        <el-form-item label="位置" prop="location">
          <el-input v-model="spaceFormData.location" placeholder="请输入车位位置" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="spaceFormData.type" placeholder="请选择类型">
            <el-option label="普通车位" :value="1" />
            <el-option label="VIP车位" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="月租价格" prop="rentPrice">
          <el-input-number
            v-model="spaceFormData.rentPrice"
            :precision="2"
            :min="0.01"
            placeholder="请输入月租价格"
          />
        </el-form-item>
        <el-form-item label="出售价格" prop="price">
          <el-input-number
            v-model="spaceFormData.price"
            :precision="2"
            :min="0"
            placeholder="请输入出售价格（可选）"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="spaceFormData.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="spaceDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitSpace" :loading="submitLoading">
          提交
        </el-button>
      </template>
    </el-dialog>

    <!-- 分配车位对话框 -->
    <el-dialog v-model="assignDialogVisible" title="分配车位" width="400px">
      <el-form :model="assignFormData" label-width="80px">
        <el-form-item label="车位编号">
          <el-input :value="assignFormData.spaceNumber" disabled />
        </el-form-item>
        <el-form-item label="业主" prop="userId">
          <el-select
            v-model="assignFormData.userId"
            placeholder="请选择业主"
            filterable
          >
            <el-option
              v-for="user in userOptions"
              :key="user.id"
              :label="user.realName"
              :value="user.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitAssign" :loading="submitLoading">
          确认分配
        </el-button>
      </template>
    </el-dialog>

    <!-- 租用车位对话框 -->
    <el-dialog v-model="rentDialogVisible" title="租用车位" width="500px">
      <el-form
        ref="rentFormRef"
        :model="rentFormData"
        :rules="rentFormRules"
        label-width="80px"
      >
        <el-form-item label="车位编号">
          <el-input :value="rentFormData.spaceNumber" disabled />
        </el-form-item>
        <el-form-item label="月租价格">
          <span style="color: #f56c6c">¥{{ rentFormData.rentPrice }}/月</span>
        </el-form-item>
        <el-form-item label="开始日期" prop="startDate">
          <el-date-picker
            v-model="rentFormData.startDate"
            type="date"
            placeholder="选择开始日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="租用月数" prop="months">
          <el-input-number
            v-model="rentFormData.months"
            :min="1"
            :max="36"
            @change="calculateRentAmount"
          />
        </el-form-item>
        <el-form-item label="总金额">
          <span style="color: #f56c6c; font-size: 18px">¥{{ rentFormData.amount }}</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rentDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitRent" :loading="submitLoading">
          确认租用
        </el-button>
      </template>
    </el-dialog>

    <!-- 续费车位对话框 -->
    <el-dialog v-model="renewDialogVisible" title="续费车位" width="500px">
      <el-form
        ref="renewFormRef"
        :model="renewFormData"
        :rules="renewFormRules"
        label-width="80px"
      >
        <el-form-item label="车位编号">
          <el-input :value="renewFormData.spaceNumber" disabled />
        </el-form-item>
        <el-form-item label="当前到期">
          <span>{{ renewFormData.currentEndDate }}</span>
        </el-form-item>
        <el-form-item label="开始日期" prop="startDate">
          <el-date-picker
            v-model="renewFormData.startDate"
            type="date"
            placeholder="选择开始日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="续费月数" prop="months">
          <el-input-number
            v-model="renewFormData.months"
            :min="1"
            :max="36"
            @change="calculateRenewAmount"
          />
        </el-form-item>
        <el-form-item label="总金额">
          <span style="color: #f56c6c; font-size: 18px">¥{{ renewFormData.amount }}</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="renewDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitRenew" :loading="submitLoading">
          确认续费
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { useUserStore } from "@/stores/user";
import {
  createParkingSpace,
  updateParkingSpace,
  getParkingSpacePage,
  deleteParkingSpace,
  assignParkingSpace,
  rentParkingSpace,
  renewParkingSpace,
  getParkingRentalPage,
  payParkingRental,
  cancelParkingRental,
} from "@/api/parking";
import { getOwnerList } from "@/api/user";

const userStore = useUserStore();
const isAdmin = computed(
  () => userStore.userInfo?.role === 1 || userStore.userInfo?.role === 2
);

const activeTab = ref("space");

// ========== 车位列表相关 ==========
const spaceLoading = ref(false);
const spaceTotal = ref(0);
const submitLoading = ref(false);
const spaceDialogVisible = ref(false);
const assignDialogVisible = ref(false);
const rentDialogVisible = ref(false);
const renewDialogVisible = ref(false);
const spaceFormRef = ref(null);
const rentFormRef = ref(null);
const renewFormRef = ref(null);

const spaceQueryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  status: null,
  type: null,
});

const spaceTableData = ref([]);
const userOptions = ref([]);

const spaceDialogTitle = computed(() =>
  spaceFormData.id ? "编辑车位" : "新增车位"
);

const spaceFormData = reactive({
  id: null,
  spaceNumber: "",
  location: "",
  type: null,
  rentPrice: null,
  price: null,
  remark: "",
});

const spaceFormRules = {
  spaceNumber: [{ required: true, message: "请输入车位编号", trigger: "blur" }],
  type: [{ required: true, message: "请选择类型", trigger: "change" }],
  rentPrice: [{ required: true, message: "请输入月租价格", trigger: "blur" }],
};

const assignFormData = reactive({
  id: null,
  spaceNumber: "",
  userId: null,
});

const rentFormData = reactive({
  spaceId: null,
  spaceNumber: "",
  rentPrice: 0,
  startDate: "",
  months: 1,
  amount: 0,
});

const rentFormRules = {
  startDate: [{ required: true, message: "请选择开始日期", trigger: "change" }],
  months: [{ required: true, message: "请输入租用月数", trigger: "blur" }],
};

const renewFormData = reactive({
  rentalId: null,
  spaceId: null,
  spaceNumber: "",
  currentEndDate: "",
  startDate: "",
  months: 1,
  amount: 0,
  rentPrice: 0,
});

const renewFormRules = {
  startDate: [{ required: true, message: "请选择开始日期", trigger: "change" }],
  months: [{ required: true, message: "请输入续费月数", trigger: "blur" }],
};

// ========== 租用记录相关 ==========
const rentalLoading = ref(false);
const rentalTotal = ref(0);

const rentalQueryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  status: null,
});

const rentalTableData = ref([]);

// ========== 工具函数 ==========
const getStatusName = (status) => {
  const names = { 0: "空闲", 1: "已出租", 2: "已出售" };
  return names[status] || "未知";
};

const getStatusType = (status) => {
  const types = { 0: "success", 1: "warning", 2: "info" };
  return types[status] || "info";
};

const getRentalStatusName = (status) => {
  const names = { 0: "待支付", 1: "已支付", 2: "已过期" };
  return names[status] || "未知";
};

const getRentalStatusType = (status) => {
  const types = { 0: "warning", 1: "success", 2: "info" };
  return types[status] || "info";
};

// ========== 车位列表方法 ==========
const fetchSpaceData = async () => {
  spaceLoading.value = true;
  try {
    const res = await getParkingSpacePage(spaceQueryParams);
    spaceTableData.value = res.data.records;
    spaceTotal.value = res.data.total;
  } catch (error) {
    console.error(error);
  } finally {
    spaceLoading.value = false;
  }
};

const fetchUserList = async () => {
  try {
    const res = await getOwnerList();
    userOptions.value = res.data;
  } catch (error) {
    console.error(error);
  }
};

const handleSpaceSearch = () => {
  spaceQueryParams.pageNum = 1;
  fetchSpaceData();
};

const handleSpaceReset = () => {
  spaceQueryParams.status = null;
  spaceQueryParams.type = null;
  handleSpaceSearch();
};

const handleAddSpace = () => {
  spaceFormData.id = null;
  spaceFormData.spaceNumber = "";
  spaceFormData.location = "";
  spaceFormData.type = null;
  spaceFormData.rentPrice = null;
  spaceFormData.price = null;
  spaceFormData.remark = "";
  spaceDialogVisible.value = true;
};

const handleEditSpace = (row) => {
  spaceFormData.id = row.id;
  spaceFormData.spaceNumber = row.spaceNumber;
  spaceFormData.location = row.location;
  spaceFormData.type = row.type;
  spaceFormData.rentPrice = row.rentPrice;
  spaceFormData.price = row.price;
  spaceFormData.remark = row.remark || "";
  spaceDialogVisible.value = true;
};

const handleSubmitSpace = async () => {
  const valid = await spaceFormRef.value.validate().catch(() => false);
  if (!valid) return;

  submitLoading.value = true;
  try {
    if (spaceFormData.id) {
      await updateParkingSpace(spaceFormData.id, spaceFormData);
      ElMessage.success("更新成功");
    } else {
      await createParkingSpace(spaceFormData);
      ElMessage.success("创建成功");
    }
    spaceDialogVisible.value = false;
    fetchSpaceData();
  } catch (error) {
    console.error(error);
  } finally {
    submitLoading.value = false;
  }
};

const handleDeleteSpace = (row) => {
  ElMessageBox.confirm("确定要删除该车位吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await deleteParkingSpace(row.id);
        ElMessage.success("删除成功");
        fetchSpaceData();
      } catch (error) {
        console.error(error);
      }
    })
    .catch(() => {});
};

const handleAssignSpace = (row) => {
  assignFormData.id = row.id;
  assignFormData.spaceNumber = row.spaceNumber;
  assignFormData.userId = null;
  assignDialogVisible.value = true;
};

const handleSubmitAssign = async () => {
  if (!assignFormData.userId) {
    ElMessage.warning("请选择业主");
    return;
  }

  submitLoading.value = true;
  try {
    await assignParkingSpace(assignFormData.id, assignFormData.userId);
    ElMessage.success("分配成功");
    assignDialogVisible.value = false;
    fetchSpaceData();
  } catch (error) {
    console.error(error);
  } finally {
    submitLoading.value = false;
  }
};

const handleRentSpace = (row) => {
  rentFormData.spaceId = row.id;
  rentFormData.spaceNumber = row.spaceNumber;
  rentFormData.rentPrice = row.rentPrice;
  rentFormData.startDate = new Date().toISOString().slice(0, 10);
  rentFormData.months = 1;
  rentFormData.amount = row.rentPrice;
  rentDialogVisible.value = true;
};

const calculateRentAmount = () => {
  rentFormData.amount = (rentFormData.rentPrice * rentFormData.months).toFixed(2);
};

const handleSubmitRent = async () => {
  const valid = await rentFormRef.value.validate().catch(() => false);
  if (!valid) return;

  // 计算结束日期
  const startDate = new Date(rentFormData.startDate);
  const endDate = new Date(startDate);
  endDate.setMonth(endDate.getMonth() + rentFormData.months);

  submitLoading.value = true;
  try {
    await rentParkingSpace({
      spaceId: rentFormData.spaceId,
      startDate: rentFormData.startDate,
      endDate: endDate.toISOString().slice(0, 10),
      amount: rentFormData.amount,
    });
    ElMessage.success("租用成功，请及时支付");
    rentDialogVisible.value = false;
    fetchSpaceData();
    fetchRentalData();
  } catch (error) {
    console.error(error);
  } finally {
    submitLoading.value = false;
  }
};

// ========== 租用记录方法 ==========
const fetchRentalData = async () => {
  rentalLoading.value = true;
  try {
    const res = await getParkingRentalPage(rentalQueryParams);
    rentalTableData.value = res.data.records;
    rentalTotal.value = res.data.total;
  } catch (error) {
    console.error(error);
  } finally {
    rentalLoading.value = false;
  }
};

const handleRentalSearch = () => {
  rentalQueryParams.pageNum = 1;
  fetchRentalData();
};

const handleRentalReset = () => {
  rentalQueryParams.status = null;
  handleRentalSearch();
};

const handlePayRental = (row) => {
  ElMessageBox.confirm("确定要支付该租用费用吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await payParkingRental(row.id);
        ElMessage.success("支付成功");
        fetchRentalData();
      } catch (error) {
        console.error(error);
      }
    })
    .catch(() => {});
};

const handleCancelRental = (row) => {
  ElMessageBox.confirm("确定要取消该租用吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await cancelParkingRental(row.id);
        ElMessage.success("取消成功");
        fetchRentalData();
        fetchSpaceData();
      } catch (error) {
        console.error(error);
      }
    })
    .catch(() => {});
};

const handleRenewSpace = async (row) => {
  // 获取车位信息
  const spaceRes = await getParkingSpacePage({});
  const space = spaceRes.data.records.find(s => s.id === row.spaceId);
  
  renewFormData.rentalId = row.id;
  renewFormData.spaceId = row.spaceId;
  renewFormData.spaceNumber = row.spaceNumber;
  renewFormData.currentEndDate = row.endDate;
  renewFormData.startDate = row.endDate;
  renewFormData.months = 1;
  renewFormData.rentPrice = space ? space.rentPrice : 0;
  renewFormData.amount = renewFormData.rentPrice;
  renewDialogVisible.value = true;
};

const calculateRenewAmount = () => {
  renewFormData.amount = (renewFormData.rentPrice * renewFormData.months).toFixed(2);
};

const handleSubmitRenew = async () => {
  const valid = await renewFormRef.value.validate().catch(() => false);
  if (!valid) return;

  // 计算结束日期
  const startDate = new Date(renewFormData.startDate);
  const endDate = new Date(startDate);
  endDate.setMonth(endDate.getMonth() + renewFormData.months);

  submitLoading.value = true;
  try {
    await renewParkingSpace(renewFormData.rentalId, {
      spaceId: renewFormData.spaceId,
      startDate: renewFormData.startDate,
      endDate: endDate.toISOString().slice(0, 10),
      amount: renewFormData.amount,
    });
    ElMessage.success("续费成功，请及时支付");
    renewDialogVisible.value = false;
    fetchRentalData();
  } catch (error) {
    console.error(error);
  } finally {
    submitLoading.value = false;
  }
};

onMounted(() => {
  fetchSpaceData();
  fetchRentalData();
  if (isAdmin.value) {
    fetchUserList();
  }
});
</script>

<style lang="scss" scoped>
.search-form {
  margin-bottom: 20px;
}
.mt-20 {
  margin-top: 20px;
}
</style>
