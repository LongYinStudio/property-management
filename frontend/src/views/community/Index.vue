<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <span>小区管理</span>
      </template>

      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="小区名称">
          <el-input
            v-model="queryParams.name"
            placeholder="请输入小区名称"
            clearable
            style="width: 220px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button v-if="isManager" type="success" @click="handleAdd">新增小区</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="小区名称" min-width="180" />
        <el-table-column prop="address" label="地址" min-width="220" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.address || "-" }}
          </template>
        </el-table-column>
        <el-table-column prop="area" label="占地面积" width="130">
          <template #default="{ row }">
            {{ row.area ? `${row.area} ㎡` : "-" }}
          </template>
        </el-table-column>
        <el-table-column prop="buildYear" label="建成年份" width="110">
          <template #default="{ row }">
            {{ row.buildYear || "-" }}
          </template>
        </el-table-column>
        <el-table-column prop="totalBuildings" label="楼栋数" width="100">
          <template #default="{ row }">
            {{ row.totalBuildings ?? "-" }}
          </template>
        </el-table-column>
        <el-table-column prop="totalRooms" label="房屋数" width="100">
          <template #default="{ row }">
            {{ row.totalRooms ?? "-" }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-button v-if="isManager" type="success" link @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="isManager" type="danger" link @click="handleDelete(row)">删除</el-button>
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

    <el-dialog
      v-model="dialogVisible"
      :title="dialogMode === 'create' ? '新增小区' : '编辑小区'"
      width="680px"
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="小区名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入小区名称" />
        </el-form-item>
        <el-form-item label="小区地址" prop="address">
          <el-input v-model="formData.address" placeholder="请输入小区地址" />
        </el-form-item>
        <div class="form-grid">
          <el-form-item label="占地面积" prop="area">
            <el-input-number
              v-model="formData.area"
              :min="0.01"
              :precision="2"
              :step="100"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="建成年份" prop="buildYear">
            <el-input-number
              v-model="formData.buildYear"
              :min="1900"
              :max="2100"
              :step="1"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="楼栋总数" prop="totalBuildings">
            <el-input-number
              v-model="formData.totalBuildings"
              :min="0"
              :step="1"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="房屋总数" prop="totalRooms">
            <el-input-number
              v-model="formData.totalRooms"
              :min="0"
              :step="1"
              style="width: 100%"
            />
          </el-form-item>
        </div>
        <el-form-item label="小区描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入小区描述，如配套设施、物业特点等"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailDialogVisible" title="小区详情" width="760px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="小区名称">{{ detailData.name || "-" }}</el-descriptions-item>
        <el-descriptions-item label="建成年份">{{ detailData.buildYear || "-" }}</el-descriptions-item>
        <el-descriptions-item label="小区地址" :span="2">
          {{ detailData.address || "-" }}
        </el-descriptions-item>
        <el-descriptions-item label="占地面积">
          {{ detailData.area ? `${detailData.area} ㎡` : "-" }}
        </el-descriptions-item>
        <el-descriptions-item label="楼栋总数">
          {{ detailData.totalBuildings ?? "-" }}
        </el-descriptions-item>
        <el-descriptions-item label="房屋总数">
          {{ detailData.totalRooms ?? "-" }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">
          {{ detailData.createTime || "-" }}
        </el-descriptions-item>
        <el-descriptions-item label="更新时间">
          {{ detailData.updateTime || "-" }}
        </el-descriptions-item>
        <el-descriptions-item label="小区描述" :span="2">
          <div class="description-content">{{ detailData.description || "-" }}</div>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { useUserStore } from "@/stores/user";
import {
  createCommunity,
  updateCommunity,
  getCommunityPage,
  getCommunityById,
  deleteCommunity,
} from "@/api/community";

const userStore = useUserStore();

const loading = ref(false);
const total = ref(0);
const submitLoading = ref(false);
const dialogVisible = ref(false);
const detailDialogVisible = ref(false);
const dialogMode = ref("create");
const currentId = ref(null);
const formRef = ref(null);
const tableData = ref([]);
const detailData = ref({});

const isManager = computed(() => userStore.userInfo?.role === 1 || userStore.userInfo?.role === 2);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  name: "",
});

const createDefaultFormData = () => ({
  name: "",
  address: "",
  area: null,
  buildYear: null,
  totalBuildings: null,
  totalRooms: null,
  description: "",
});

const formData = reactive(createDefaultFormData());

const formRules = {
  name: [{ required: true, message: "请输入小区名称", trigger: "blur" }],
  address: [{ max: 255, message: "地址长度不能超过255位", trigger: "blur" }],
  description: [{ max: 2000, message: "描述长度不能超过2000位", trigger: "blur" }],
};

const fetchData = async () => {
  loading.value = true;
  try {
    const res = await getCommunityPage(queryParams);
    tableData.value = res.data.records || [];
    total.value = res.data.total || 0;
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  queryParams.pageNum = 1;
  fetchData();
};

const handleReset = () => {
  queryParams.pageNum = 1;
  queryParams.pageSize = 10;
  queryParams.name = "";
  fetchData();
};

const handleAdd = () => {
  dialogMode.value = "create";
  currentId.value = null;
  Object.assign(formData, createDefaultFormData());
  formRef.value?.clearValidate();
  dialogVisible.value = true;
};

const handleEdit = async (row) => {
  dialogMode.value = "edit";
  currentId.value = row.id;
  const res = await getCommunityById(row.id);
  Object.assign(formData, {
    name: res.data.name,
    address: res.data.address,
    area: res.data.area ? Number(res.data.area) : null,
    buildYear: res.data.buildYear,
    totalBuildings: res.data.totalBuildings,
    totalRooms: res.data.totalRooms,
    description: res.data.description,
  });
  formRef.value?.clearValidate();
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  await formRef.value.validate();
  submitLoading.value = true;
  try {
    if (dialogMode.value === "create") {
      await createCommunity(formData);
      ElMessage.success("小区创建成功");
    } else {
      await updateCommunity(currentId.value, formData);
      ElMessage.success("小区更新成功");
    }
    dialogVisible.value = false;
    fetchData();
  } finally {
    submitLoading.value = false;
  }
};

const handleView = async (row) => {
  const res = await getCommunityById(row.id);
  detailData.value = res.data;
  detailDialogVisible.value = true;
};

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`确定删除小区“${row.name}”吗？`, "提示", {
    type: "warning",
  });
  await deleteCommunity(row.id);
  ElMessage.success("小区删除成功");
  fetchData();
};

const resetForm = () => {
  Object.assign(formData, createDefaultFormData());
  formRef.value?.clearValidate();
};

onMounted(() => {
  fetchData();
});
</script>

<style lang="scss" scoped>
.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 12px;
}

.description-content {
  white-space: pre-wrap;
  line-height: 1.6;
}

@media (max-width: 768px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
