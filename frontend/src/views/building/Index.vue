<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <span>楼栋管理</span>
      </template>

      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="所属小区">
          <el-select
            v-model="queryParams.communityId"
            placeholder="请选择小区"
            clearable
            filterable
            style="width: 220px"
          >
            <el-option
              v-for="item in communityOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="楼栋名称">
          <el-input
            v-model="queryParams.name"
            placeholder="请输入楼栋名称"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button v-if="isManager" type="success" @click="handleAdd">新增楼栋</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="communityName" label="所属小区" min-width="180">
          <template #default="{ row }">
            {{ row.communityName || "-" }}
          </template>
        </el-table-column>
        <el-table-column prop="name" label="楼栋名称" min-width="140" />
        <el-table-column prop="floors" label="楼层数" width="100">
          <template #default="{ row }">
            {{ row.floors ?? "-" }}
          </template>
        </el-table-column>
        <el-table-column prop="units" label="单元数" width="100">
          <template #default="{ row }">
            {{ row.units ?? "-" }}
          </template>
        </el-table-column>
        <el-table-column prop="roomsPerFloor" label="每层房数" width="110">
          <template #default="{ row }">
            {{ row.roomsPerFloor ?? "-" }}
          </template>
        </el-table-column>
        <el-table-column prop="roomCount" label="已登记房屋" width="120">
          <template #default="{ row }">
            {{ row.roomCount ?? 0 }}
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
      :title="dialogMode === 'create' ? '新增楼栋' : '编辑楼栋'"
      width="640px"
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="96px">
        <el-form-item label="所属小区" prop="communityId">
          <el-select
            v-model="formData.communityId"
            placeholder="请选择小区"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="item in communityOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="楼栋名称" prop="name">
          <el-input v-model="formData.name" placeholder="如：1号楼、A栋" />
        </el-form-item>
        <div class="form-grid">
          <el-form-item label="楼层数" prop="floors">
            <el-input-number v-model="formData.floors" :min="1" :step="1" style="width: 100%" />
          </el-form-item>
          <el-form-item label="单元数" prop="units">
            <el-input-number v-model="formData.units" :min="1" :step="1" style="width: 100%" />
          </el-form-item>
          <el-form-item label="每层房数" prop="roomsPerFloor">
            <el-input-number
              v-model="formData.roomsPerFloor"
              :min="1"
              :step="1"
              style="width: 100%"
            />
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailDialogVisible" title="楼栋详情" width="680px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="所属小区">{{ detailData.communityName || "-" }}</el-descriptions-item>
        <el-descriptions-item label="楼栋名称">{{ detailData.name || "-" }}</el-descriptions-item>
        <el-descriptions-item label="楼层数">{{ detailData.floors ?? "-" }}</el-descriptions-item>
        <el-descriptions-item label="单元数">{{ detailData.units ?? "-" }}</el-descriptions-item>
        <el-descriptions-item label="每层房数">{{ detailData.roomsPerFloor ?? "-" }}</el-descriptions-item>
        <el-descriptions-item label="已登记房屋">{{ detailData.roomCount ?? 0 }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData.createTime || "-" }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ detailData.updateTime || "-" }}</el-descriptions-item>
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
  createBuilding,
  updateBuilding,
  getBuildingPage,
  getBuildingById,
  deleteBuilding,
} from "@/api/building";
import { getCommunityList } from "@/api/community";

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
const communityOptions = ref([]);

const isManager = computed(() => userStore.userInfo?.role === 1 || userStore.userInfo?.role === 2);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  communityId: null,
  name: "",
});

const createDefaultFormData = () => ({
  communityId: null,
  name: "",
  floors: null,
  units: null,
  roomsPerFloor: null,
});

const formData = reactive(createDefaultFormData());

const formRules = {
  communityId: [{ required: true, message: "请选择所属小区", trigger: "change" }],
  name: [{ required: true, message: "请输入楼栋名称", trigger: "blur" }],
};

const loadCommunityOptions = async () => {
  const res = await getCommunityList();
  communityOptions.value = res.data || [];
};

const fetchData = async () => {
  loading.value = true;
  try {
    const res = await getBuildingPage(queryParams);
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
  queryParams.communityId = null;
  queryParams.name = "";
  fetchData();
};

const handleAdd = async () => {
  dialogMode.value = "create";
  currentId.value = null;
  Object.assign(formData, createDefaultFormData());
  await loadCommunityOptions();
  formRef.value?.clearValidate();
  dialogVisible.value = true;
};

const handleEdit = async (row) => {
  dialogMode.value = "edit";
  currentId.value = row.id;
  await loadCommunityOptions();
  const res = await getBuildingById(row.id);
  Object.assign(formData, {
    communityId: res.data.communityId,
    name: res.data.name,
    floors: res.data.floors,
    units: res.data.units,
    roomsPerFloor: res.data.roomsPerFloor,
  });
  formRef.value?.clearValidate();
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  await formRef.value.validate();
  submitLoading.value = true;
  try {
    if (dialogMode.value === "create") {
      await createBuilding(formData);
      ElMessage.success("楼栋创建成功");
    } else {
      await updateBuilding(currentId.value, formData);
      ElMessage.success("楼栋更新成功");
    }
    dialogVisible.value = false;
    fetchData();
  } finally {
    submitLoading.value = false;
  }
};

const handleView = async (row) => {
  const res = await getBuildingById(row.id);
  detailData.value = res.data;
  detailDialogVisible.value = true;
};

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`确定删除楼栋“${row.name}”吗？`, "提示", {
    type: "warning",
  });
  await deleteBuilding(row.id);
  ElMessage.success("楼栋删除成功");
  fetchData();
};

const resetForm = () => {
  Object.assign(formData, createDefaultFormData());
  formRef.value?.clearValidate();
};

onMounted(async () => {
  await loadCommunityOptions();
  fetchData();
});
</script>

<style lang="scss" scoped>
.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 12px;
}

@media (max-width: 768px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
