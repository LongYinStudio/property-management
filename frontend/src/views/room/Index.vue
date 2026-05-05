<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <span>房屋管理</span>
      </template>

      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="所属小区">
          <el-select
            v-model="queryParams.communityId"
            placeholder="请选择小区"
            clearable
            filterable
            style="width: 220px"
            @change="handleQueryCommunityChange"
          >
            <el-option
              v-for="item in communityOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="所属楼栋">
          <el-select
            v-model="queryParams.buildingId"
            placeholder="请选择楼栋"
            clearable
            filterable
            :disabled="!queryParams.communityId"
            style="width: 220px"
          >
            <el-option
              v-for="item in queryBuildingOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="房号">
          <el-input
            v-model="queryParams.roomNumber"
            placeholder="请输入房号"
            clearable
            style="width: 160px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="queryParams.status"
            placeholder="请选择状态"
            clearable
            style="width: 140px"
          >
            <el-option label="空置" :value="0" />
            <el-option label="已入住" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button v-if="isManager" type="success" @click="handleAdd">新增房屋</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="communityName" label="所属小区" min-width="160">
          <template #default="{ row }">
            {{ row.communityName || "-" }}
          </template>
        </el-table-column>
        <el-table-column prop="buildingName" label="楼栋" min-width="120">
          <template #default="{ row }">
            {{ row.buildingName || "-" }}
          </template>
        </el-table-column>
        <el-table-column prop="displayName" label="房屋" min-width="180">
          <template #default="{ row }">
            {{ row.displayName || row.roomNumber || "-" }}
          </template>
        </el-table-column>
        <el-table-column prop="floor" label="楼层" width="90">
          <template #default="{ row }">
            {{ row.floor ?? "-" }}
          </template>
        </el-table-column>
        <el-table-column prop="area" label="面积" width="110">
          <template #default="{ row }">
            {{ row.area ? `${row.area} ㎡` : "-" }}
          </template>
        </el-table-column>
        <el-table-column prop="ownerName" label="绑定业主" width="120">
          <template #default="{ row }">
            {{ row.ownerName || "-" }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.statusName }}
            </el-tag>
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
      :title="dialogMode === 'create' ? '新增房屋' : '编辑房屋'"
      width="700px"
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="96px">
        <div class="form-grid">
          <el-form-item label="所属小区" prop="communityId">
            <el-select
              v-model="formData.communityId"
              placeholder="请选择小区"
              filterable
              style="width: 100%"
              @change="handleFormCommunityChange"
            >
              <el-option
                v-for="item in communityOptions"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="所属楼栋" prop="buildingId">
            <el-select
              v-model="formData.buildingId"
              placeholder="请选择楼栋"
              filterable
              :disabled="!formData.communityId"
              style="width: 100%"
            >
              <el-option
                v-for="item in formBuildingOptions"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="单元" prop="unit">
            <el-input v-model="formData.unit" placeholder="如：1、A" />
          </el-form-item>
          <el-form-item label="楼层" prop="floor">
            <el-input-number v-model="formData.floor" :min="1" :step="1" style="width: 100%" />
          </el-form-item>
          <el-form-item label="房号" prop="roomNumber">
            <el-input v-model="formData.roomNumber" placeholder="如：1201" />
          </el-form-item>
          <el-form-item label="面积" prop="area">
            <el-input-number
              v-model="formData.area"
              :min="0.01"
              :precision="2"
              :step="1"
              style="width: 100%"
            />
          </el-form-item>
        </div>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="0">空置</el-radio>
            <el-radio :label="1">已入住</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailDialogVisible" title="房屋详情" width="760px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="所属小区">{{ detailData.communityName || "-" }}</el-descriptions-item>
        <el-descriptions-item label="所属楼栋">{{ detailData.buildingName || "-" }}</el-descriptions-item>
        <el-descriptions-item label="房屋名称">{{ detailData.displayName || "-" }}</el-descriptions-item>
        <el-descriptions-item label="房号">{{ detailData.roomNumber || "-" }}</el-descriptions-item>
        <el-descriptions-item label="楼层">{{ detailData.floor ?? "-" }}</el-descriptions-item>
        <el-descriptions-item label="面积">
          {{ detailData.area ? `${detailData.area} ㎡` : "-" }}
        </el-descriptions-item>
        <el-descriptions-item label="绑定业主">{{ detailData.ownerName || "-" }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="detailData.status === 1 ? 'success' : 'info'">
            {{ detailData.statusName || "-" }}
          </el-tag>
        </el-descriptions-item>
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
  createRoom,
  updateRoom,
  getRoomPage,
  getRoomById,
  deleteRoom,
} from "@/api/room";
import { getCommunityList } from "@/api/community";
import { getBuildingList } from "@/api/building";

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
const queryBuildingOptions = ref([]);
const formBuildingOptions = ref([]);

const isManager = computed(() => userStore.userInfo?.role === 1 || userStore.userInfo?.role === 2);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  communityId: null,
  buildingId: null,
  roomNumber: "",
  status: null,
});

const createDefaultFormData = () => ({
  communityId: null,
  buildingId: null,
  unit: "",
  floor: null,
  roomNumber: "",
  area: null,
  status: 0,
});

const formData = reactive(createDefaultFormData());

const formRules = {
  communityId: [{ required: true, message: "请选择所属小区", trigger: "change" }],
  buildingId: [{ required: true, message: "请选择所属楼栋", trigger: "change" }],
  roomNumber: [{ required: true, message: "请输入房号", trigger: "blur" }],
};

const loadCommunityOptions = async () => {
  const res = await getCommunityList();
  communityOptions.value = res.data || [];
};

const loadBuildingOptions = async (communityId, target) => {
  if (!communityId) {
    target.value = [];
    return;
  }
  const res = await getBuildingList({ communityId });
  target.value = res.data || [];
};

const fetchData = async () => {
  loading.value = true;
  try {
    const res = await getRoomPage(queryParams);
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
  queryParams.buildingId = null;
  queryParams.roomNumber = "";
  queryParams.status = null;
  queryBuildingOptions.value = [];
  fetchData();
};

const handleQueryCommunityChange = async (value) => {
  queryParams.buildingId = null;
  await loadBuildingOptions(value, queryBuildingOptions);
};

const handleFormCommunityChange = async (value) => {
  formData.buildingId = null;
  await loadBuildingOptions(value, formBuildingOptions);
};

const handleAdd = async () => {
  dialogMode.value = "create";
  currentId.value = null;
  Object.assign(formData, createDefaultFormData());
  await loadCommunityOptions();
  formBuildingOptions.value = [];
  formRef.value?.clearValidate();
  dialogVisible.value = true;
};

const handleEdit = async (row) => {
  dialogMode.value = "edit";
  currentId.value = row.id;
  await loadCommunityOptions();
  const res = await getRoomById(row.id);
  Object.assign(formData, {
    communityId: res.data.communityId,
    buildingId: null,
    unit: res.data.unit,
    floor: res.data.floor,
    roomNumber: res.data.roomNumber,
    area: res.data.area ? Number(res.data.area) : null,
    status: res.data.status,
  });
  await loadBuildingOptions(res.data.communityId, formBuildingOptions);
  formData.buildingId = res.data.buildingId;
  formRef.value?.clearValidate();
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  await formRef.value.validate();
  submitLoading.value = true;
  try {
    const payload = {
      buildingId: formData.buildingId,
      unit: formData.unit,
      floor: formData.floor,
      roomNumber: formData.roomNumber,
      area: formData.area,
      status: formData.status,
    };
    if (dialogMode.value === "create") {
      await createRoom(payload);
      ElMessage.success("房屋创建成功");
    } else {
      await updateRoom(currentId.value, payload);
      ElMessage.success("房屋更新成功");
    }
    dialogVisible.value = false;
    fetchData();
  } finally {
    submitLoading.value = false;
  }
};

const handleView = async (row) => {
  const res = await getRoomById(row.id);
  detailData.value = res.data;
  detailDialogVisible.value = true;
};

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`确定删除房屋“${row.displayName || row.roomNumber}”吗？`, "提示", {
    type: "warning",
  });
  await deleteRoom(row.id);
  ElMessage.success("房屋删除成功");
  fetchData();
};

const resetForm = () => {
  Object.assign(formData, createDefaultFormData());
  formBuildingOptions.value = [];
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
