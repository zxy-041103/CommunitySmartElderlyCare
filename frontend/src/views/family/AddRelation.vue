<template>
  <div class="add-relation-container">
    <div class="page-header">
      <h2 class="page-title">添加关联关系</h2>
      <el-button type="primary" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
        返回
      </el-button>
    </div>

    <el-card class="relation-form-card">
      <el-form ref="relationFormRef" :model="relationForm" :rules="rules" label-width="120px">
        <el-form-item label="老人" prop="elderlyId">
          <el-select
            v-model="relationForm.elderlyId"
            placeholder="请选择老人"
            style="width: 100%"
          >
            <el-option
              v-for="elderly in elderlyList"
              :key="elderly.id"
              :label="elderly.realName"
              :value="elderly.id"
            >
              <div class="option-content">
                <span>{{ elderly.realName }}</span>
                <span class="option-phone">{{ elderly.phone }}</span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="关系类型" prop="relationType">
          <el-select
            v-model="relationForm.relationType"
            placeholder="请选择关系类型"
            style="width: 100%"
          >
            <el-option label="配偶" value="spouse" />
            <el-option label="子女" value="child" />
            <el-option label="孙子女" value="grandchild" />
            <el-option label="兄弟姐妹" value="sibling" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>

        <el-form-item label="关系名称" prop="relationName">
          <el-input
            v-model="relationForm.relationName"
            placeholder="请输入具体关系名称（如：儿子、女儿等）"
          />
        </el-form-item>

        <el-form-item label="审核材料" prop="proofMaterials">
          <el-upload
            class="upload-demo"
            action="#"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
            :file-list="fileList"
            :auto-upload="false"
            multiple
            accept="image/*"
          >
            <el-button type="primary">
              <el-icon><Plus /></el-icon>
              上传证明材料
            </el-button>
            <template #tip>
              <div class="el-upload__tip">
                请上传关系证明材料，如户口本、身份证等照片
              </div>
            </template>
          </el-upload>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="loading">
            提交申请
          </el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { defineOptions } from "vue";
import { ref, onMounted } from "vue";

// 页面名称
defineOptions({
  name: 'FamilyAddRelation'
});
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { ArrowLeft, Plus } from "@element-plus/icons-vue";
import request from "@/utils/request";
import { useUserStore } from "@/store/user";

const router = useRouter();
const userStore = useUserStore();

const relationFormRef = ref(null);
const loading = ref(false);
const elderlyList = ref([]);
const fileList = ref([]);

const relationForm = ref({
  elderlyId: null,
  relationType: null,
  relationName: "",
  proofMaterials: ""
});

const rules = {
  elderlyId: [
    { required: true, message: "请选择老人", trigger: "change" }
  ],
  relationType: [
    { required: true, message: "请选择关系类型", trigger: "change" }
  ],
  relationName: [
    { required: true, message: "请输入关系名称", trigger: "blur" }
  ],
  proofMaterials: [
    { required: true, message: "请上传审核材料", trigger: "change" }
  ]
};

const fetchElderlyList = async () => {
  try {
    const response = await request({
      url: '/user/list',
      method: 'get',
      params: {
        roleType: 'elderly',
        page: 1,
        size: 100
      }
    });
    if (response.code === 200 && response.data) {
      elderlyList.value = response.data.records || [];
    }
  } catch (error) {
    console.error('获取老人列表失败:', error);
    ElMessage.error('获取老人列表失败');
  }
};

const handleFileChange = (file, files) => {
  console.log('文件变化:', file, files);
  fileList.value = files;
  // 更新proofMaterials字段，用于表单验证
  relationForm.value.proofMaterials = files.length > 0 ? 'uploaded' : '';
};

const handleFileRemove = (file, files) => {
  console.log('文件移除:', file, files);
  fileList.value = files;
  // 更新proofMaterials字段，用于表单验证
  relationForm.value.proofMaterials = files.length > 0 ? 'uploaded' : '';
};

const submitForm = async () => {
  if (!relationFormRef.value) return;

  try {
    await relationFormRef.value.validate();
    loading.value = true;

    // 第一步：先创建关联关系（不带证明材料），获取relationId
    const createResponse = await request({
      url: '/user/relation',
      method: 'post',
      params: {
        familyId: userStore.userId,
        elderlyId: relationForm.value.elderlyId,
        relationType: relationForm.value.relationType,
        relationName: relationForm.value.relationName,
        proofMaterials: null
      }
    });

    if (createResponse.code !== 200) {
      ElMessage.error(createResponse.message || '创建关联关系失败');
      return;
    }

    // 获取新创建的关联关系ID
    const relationId = createResponse.data;
    
    if (!relationId) {
      ElMessage.error('获取关联关系ID失败');
      return;
    }

    // 第二步：上传图片，使用relationId作为文件名
    const formData = new FormData();
    fileList.value.forEach(file => {
      formData.append('files', file.raw);
    });
    formData.append('relationId', relationId);

    const uploadResponse = await request({
      url: '/relation-proof/upload',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });

    if (uploadResponse.code !== 200) {
      ElMessage.error('文件上传失败');
      return;
    }

    // 第三步：更新关联关系记录，添加证明材料信息
    const proofMaterials = uploadResponse.data.map((url, index) => ({
      name: fileList.value[index].name,
      url: url,
      size: fileList.value[index].size
    }));

    const updateResponse = await request({
      url: `/user/relation/${relationId}/update-proof`,
      method: 'put',
      params: {
        proofMaterials: JSON.stringify(proofMaterials)
      }
    });

    if (updateResponse.code === 200) {
      ElMessage.success('关联关系申请已提交，等待审核');
      router.push('/family/monitoring');
    } else {
      ElMessage.error(updateResponse.message || '更新证明材料失败');
    }
  } catch (error) {
    console.error('提交失败:', error);
    ElMessage.error('提交失败');
  } finally {
    loading.value = false;
  }
};

const resetForm = () => {
  relationFormRef.value?.resetFields();
  fileList.value = [];
};

const goBack = () => {
  router.push('/family/monitoring');
};

onMounted(() => {
  fetchElderlyList();
});
</script>

<style lang="scss" scoped>
.add-relation-container {
  padding: 30px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.page-title {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin: 0;
}

.relation-form-card {
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  padding: 30px;
}

.option-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.option-phone {
  font-size: 12px;
  color: #909399;
}

.upload-demo {
  margin-top: 10px;
}

@media (max-width: 768px) {
  .add-relation-container {
    padding: 20px;
  }

  .relation-form-card {
    padding: 20px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
}
</style>