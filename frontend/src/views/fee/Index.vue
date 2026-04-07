<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <span>物业费管理</span>
      </template>
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="状态">
          <el-select
            v-model="queryParams.status"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
          >
            <el-option label="未支付" :value="0" />
            <el-option label="已支付" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="费用类型">
          <el-select
            v-model="queryParams.type"
            placeholder="请选择类型"
            clearable
            style="width: 120px"
          >
            <el-option label="物业费" :value="1" />
            <el-option label="停车费" :value="2" />
            <el-option label="水费" :value="3" />
            <el-option label="电费" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="年份">
          <el-select
            v-model="queryParams.year"
            placeholder="请选择年份"
            clearable
            style="width: 120px"
          >
            <el-option
              v-for="year in yearOptions"
              :key="year"
              :label="year"
              :value="year"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button
            type="success"
            @click="handleAdd"
            v-if="isAdmin"
            >新增费用</el-button
          >
          <el-button type="warning" @click="handleExport">导出Excel</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userName" label="用户" v-if="isAdmin" />
        <el-table-column prop="type" label="费用类型">
          <template #default="{ row }">
            <el-tag>{{ getTypeName(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="金额">
          <template #default="{ row }">
            <span style="color: #f56c6c">¥{{ row.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="year" label="年份" />
        <el-table-column prop="month" label="月份">
          <template #default="{ row }"> {{ row.month }}月 </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'">
              {{ row.status === 1 ? "已支付" : "未支付" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              type="success"
              link
              @click="handlePay(row)"
              v-if="row.status === 0"
              >支付</el-button
            >
            <el-button type="primary" link @click="handleView(row)"
              >查看</el-button
            >
            <el-button
              type="danger"
              link
              @click="handleDelete(row)"
              v-if="isAdmin"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        class="mt-20"
        @size-change="fetchData"
        @current-change="fetchData"
      />
    </el-card>

    <!-- 新增费用对话框 -->
    <el-dialog v-model="dialogVisible" title="新增费用" width="500px">
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="80px"
      >
        <el-form-item label="用户" prop="userId">
          <el-select
            v-model="formData.userId"
            placeholder="请选择用户"
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
        <el-form-item label="费用类型" prop="type">
          <el-select v-model="formData.type" placeholder="请选择费用类型">
            <el-option label="物业费" :value="1" />
            <el-option label="停车费" :value="2" />
            <el-option label="水费" :value="3" />
            <el-option label="电费" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="年份" prop="year">
          <el-select v-model="formData.year" placeholder="请选择年份">
            <el-option
              v-for="year in yearOptions"
              :key="year"
              :label="year"
              :value="year"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="月份" prop="month">
          <el-select v-model="formData.month" placeholder="请选择月份">
            <el-option
              v-for="month in 12"
              :key="month"
              :label="`${month}月`"
              :value="month"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="金额" prop="amount">
          <el-input-number
            v-model="formData.amount"
            :precision="2"
            :min="0.01"
            placeholder="请输入金额"
          />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          提交
        </el-button>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog v-model="viewDialogVisible" title="费用详情" width="500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="用户">{{
          viewData.userName
        }}</el-descriptions-item>
        <el-descriptions-item label="费用类型">{{
          getTypeName(viewData.type)
        }}</el-descriptions-item>
        <el-descriptions-item label="金额">
          <span style="color: #f56c6c">¥{{ viewData.amount }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="年份">{{
          viewData.year
        }}</el-descriptions-item>
        <el-descriptions-item label="月份">
          {{ viewData.month }}月
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="viewData.status === 1 ? 'success' : 'warning'">
            {{ viewData.status === 1 ? "已支付" : "未支付" }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="描述">{{
          viewData.description || "无"
        }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{
          viewData.createTime
        }}</el-descriptions-item>
        <el-descriptions-item label="支付时间" v-if="viewData.payTime">{{
          viewData.payTime
        }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { useUserStore } from "@/stores/user";
import {
  createPropertyFee,
  getPropertyFeePage,
  getPropertyFeeById,
  payPropertyFee,
  deletePropertyFee,
  exportPropertyFee,
} from "@/api/propertyFee";
import { getOwnerList } from "@/api/user";

const userStore = useUserStore();
const isAdmin = computed(
  () => userStore.userInfo?.role === 1 || userStore.userInfo?.role === 2
);

const loading = ref(false);
const total = ref(0);
const submitLoading = ref(false);
const dialogVisible = ref(false);
const viewDialogVisible = ref(false);
const formRef = ref(null);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  status: null,
  type: null,
  year: null,
});

const tableData = ref([]);
const userOptions = ref([]);

// 生成年份选项
const currentYear = new Date().getFullYear();
const yearOptions = computed(() => {
  const years = [];
  for (let i = currentYear - 2; i <= currentYear + 1; i++) {
    years.push(i);
  }
  return years;
});

const formData = reactive({
  userId: null,
  type: null,
  year: null,
  month: null,
  amount: null,
  description: "",
});

const formRules = {
  userId: [{ required: true, message: "请选择用户", trigger: "change" }],
  type: [{ required: true, message: "请选择费用类型", trigger: "change" }],
  year: [{ required: true, message: "请选择年份", trigger: "change" }],
  month: [{ required: true, message: "请选择月份", trigger: "change" }],
  amount: [{ required: true, message: "请输入金额", trigger: "blur" }],
};

const viewData = ref({});

const getTypeName = (type) => {
  const names = { 1: "物业费", 2: "停车费", 3: "水费", 4: "电费" };
  return names[type] || "未知";
};

const fetchData = async () => {
  loading.value = true;
  try {
    const res = await getPropertyFeePage(queryParams);
    tableData.value = res.data.records;
    total.value = res.data.total;
  } catch (error) {
    console.error(error);
  } finally {
    loading.value = false;
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

const handleSearch = () => {
  queryParams.pageNum = 1;
  fetchData();
};

const handleReset = () => {
  queryParams.status = null;
  queryParams.type = null;
  queryParams.year = null;
  handleSearch();
};

const handleAdd = () => {
  formData.userId = null;
  formData.type = null;
  formData.year = currentYear;
  formData.month = null;
  formData.amount = null;
  formData.description = "";
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false);
  if (!valid) return;

  submitLoading.value = true;
  try {
    await createPropertyFee(formData);
    ElMessage.success("创建成功");
    dialogVisible.value = false;
    fetchData();
  } catch (error) {
    console.error(error);
  } finally {
    submitLoading.value = false;
  }
};

const handlePay = (row) => {
  ElMessageBox.confirm("确定要支付该费用吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await payPropertyFee(row.id);
        ElMessage.success("支付成功");
        fetchData();
      } catch (error) {
        console.error(error);
      }
    })
    .catch(() => {});
};

const handleView = async (row) => {
  try {
    const res = await getPropertyFeeById(row.id);
    viewData.value = res.data;
    viewDialogVisible.value = true;
  } catch (error) {
    console.error(error);
  }
};

const handleDelete = (row) => {
  ElMessageBox.confirm("确定要删除该费用记录吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await deletePropertyFee(row.id);
        ElMessage.success("删除成功");
        fetchData();
      } catch (error) {
        console.error(error);
      }
    })
    .catch(() => {});
};

const handleExport = async () => {
  try {
    const res = await exportPropertyFee({
      status: queryParams.status,
      type: queryParams.type,
      year: queryParams.year,
    });
    const blob = new Blob([res], {
      type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
    });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.download = `物业费记录_${new Date().toISOString().slice(0, 10)}.xlsx`;
    link.click();
    window.URL.revokeObjectURL(url);
    ElMessage.success("导出成功");
  } catch (error) {
    console.error(error);
  }
};

onMounted(() => {
  fetchData();
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

