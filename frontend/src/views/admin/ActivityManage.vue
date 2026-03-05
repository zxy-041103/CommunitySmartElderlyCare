<template>
  <div class="activity-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>活动管理</span>
          <el-button
            type="primary"
            @click="handleAddActivity"
          >
            <el-icon><Plus /></el-icon>
            新增活动
          </el-button>
        </div>
      </template>

      <!-- 活动列表 -->
      <el-table
        v-loading="loading"
        :data="activityList"
        style="width: 100%"
        border
      >
        <el-table-column prop="id" label="活动ID" width="80" />
        <el-table-column prop="title" label="活动标题" min-width="200" />
        <el-table-column label="活动分类" width="120">
          <template #default="scope">
            {{ getCategoryName(scope.row.activityCategoryId) }}
          </template>
        </el-table-column>
        <el-table-column prop="activityTime" label="活动时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.activityTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="activityLocation" label="活动地点" width="200" />
        <el-table-column prop="participantCount" label="参与人数" width="100" />
        <el-table-column prop="viewCount" label="浏览次数" width="100" />
        <el-table-column prop="publishTime" label="发布时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.publishTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              @click="handleEditActivity(scope.row)"
            >
              编辑
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleDeleteActivity(scope.row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination" v-if="total > 0">
        <el-pagination
          :current-page="page"
          :page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>

      <!-- 活动编辑弹窗 -->
      <el-dialog
        v-model="dialogVisible"
        :title="isEdit ? '编辑活动' : '新增活动'"
        width="800px"
      >
        <el-form
          ref="activityFormRef"
          :model="activityForm"
          :rules="activityRules"
          label-width="120px"
        >
          <el-form-item label="活动标题" prop="title">
            <el-input v-model="activityForm.title" placeholder="请输入活动标题" />
          </el-form-item>
          <el-form-item label="活动分类" prop="activityCategoryId">
            <el-select v-model="activityForm.activityCategoryId" placeholder="请选择活动分类">
              <el-option
                v-for="category in categories"
                :key="category.id"
                :label="category.categoryName"
                :value="category.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="活动时间" prop="activityTime">
            <el-date-picker
              v-model="activityForm.activityTime"
              type="datetime"
              placeholder="请选择活动时间"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="活动地点" prop="activityLocation">
            <el-input v-model="activityForm.activityLocation" placeholder="请输入活动地点" />
          </el-form-item>
          <el-form-item label="参与人数" prop="participantCount">
            <el-input-number v-model="activityForm.participantCount" :min="0" placeholder="请输入参与人数" />
          </el-form-item>
          <el-form-item label="目标角色" prop="targetRole">
            <el-checkbox-group v-model="targetRoles">
              <el-checkbox label="elderly">老人</el-checkbox>
              <el-checkbox label="family">家属</el-checkbox>
              <el-checkbox label="caregiver">护工</el-checkbox>
            </el-checkbox-group>
          </el-form-item>
          <el-form-item label="是否置顶" prop="isTop">
            <el-switch v-model="activityForm.isTop" />
          </el-form-item>
          <el-form-item label="是否发布" prop="isPublished">
            <el-switch v-model="activityForm.isPublished" />
          </el-form-item>
          <el-form-item label="发布时间" prop="publishTime">
            <el-date-picker
              v-model="activityForm.publishTime"
              type="datetime"
              placeholder="请选择发布时间"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="活动内容" prop="content">
            <el-input
              v-model="activityForm.content"
              type="textarea"
              placeholder="请输入活动内容"
              :rows="5"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="submitForm">确定</el-button>
          </span>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import request from '@/utils/request'

// 页面名称
defineOptions({
  name: 'AdminActivityManage'
});
// 活动列表数据
const loading = ref(false)
const activityList = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)

// 活动分类
const categories = ref([])

// 活动编辑表单
const dialogVisible = ref(false)
const isEdit = ref(false)
const activityFormRef = ref(null)
const activityForm = ref({
  id: null,
  title: '',
  content: '',
  activityCategoryId: null,
  activityTime: null,
  activityLocation: '',
  participantCount: 0,
  targetRole: 'elderly,family',
  isTop: 0,
  isPublished: 1,
  publishTime: new Date()
})

const targetRoles = ref(['elderly', 'family'])

const activityRules = {
  title: [{ required: true, message: '请输入活动标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入活动内容', trigger: 'blur' }],
  activityCategoryId: [{ required: true, message: '请选择活动分类', trigger: 'change' }],
  activityTime: [{ required: true, message: '请选择活动时间', trigger: 'change' }],
  activityLocation: [{ required: true, message: '请输入活动地点', trigger: 'blur' }],
  publishTime: [{ required: true, message: '请选择发布时间', trigger: 'change' }]
}

// 加载活动列表
const loadActivities = async () => {
  loading.value = true
  try {
    const res = await request({
      url: '/admin/activities',
      method: 'get',
      params: { page: page.value, size: pageSize.value }
    })
    if (res.code === 200) {
      activityList.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
    console.error('获取活动列表失败:', error)
    ElMessage.error('获取活动列表失败')
  } finally {
    loading.value = false
  }
}

// 加载活动分类
const loadCategories = async () => {
  try {
    const res = await request({
      url: '/admin/activities/categories',
      method: 'get'
    })
    if (res.code === 200) {
      categories.value = res.data
    }
  } catch (error) {
    console.error('获取活动分类失败:', error)
  }
}

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  page.value = 1
  loadActivities()
}

const handleCurrentChange = (current) => {
  page.value = current
  loadActivities()
}

// 新增活动
const handleAddActivity = () => {
  isEdit.value = false
  activityForm.value = {
    id: null,
    title: '',
    content: '',
    activityCategoryId: null,
    activityTime: null,
    activityLocation: '',
    participantCount: 0,
    targetRole: 'elderly,family',
    isTop: 0,
    isPublished: 1,
    publishTime: new Date()
  }
  targetRoles.value = ['elderly', 'family']
  dialogVisible.value = true
}

// 编辑活动
const handleEditActivity = (activity) => {
  isEdit.value = true
  activityForm.value = { ...activity }
  // 处理目标角色
  targetRoles.value = activity.targetRole ? activity.targetRole.split(',') : ['elderly', 'family']
  dialogVisible.value = true
}

// 删除活动
const handleDeleteActivity = async (activity) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除活动 "${activity.title}" 吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const res = await request({
      url: `/admin/activities/${activity.id}`,
      method: 'delete'
    })
    
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadActivities()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除活动失败:', error)
      ElMessage.error('删除活动失败')
    }
  }
}

// 提交表单
const submitForm = async () => {
  if (!activityFormRef.value) return
  
  try {
    await activityFormRef.value.validate()
    
    // 处理目标角色
    activityForm.value.targetRole = targetRoles.value.join(',')
    
    let res
    if (isEdit.value) {
      res = await request({
        url: `/admin/activities/${activityForm.value.id}`,
        method: 'put',
        data: activityForm.value
      })
    } else {
      res = await request({
        url: '/admin/activities',
        method: 'post',
        data: activityForm.value
      })
    }
    
    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
      dialogVisible.value = false
      loadActivities()
    }
  } catch (error) {
    console.error('提交表单失败:', error)
  }
}

// 获取分类名称
const getCategoryName = (categoryId) => {
  const category = categories.value.find(c => c.id === categoryId)
  return category ? category.categoryName : '未分类'
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '--'
  try {
    const date = new Date(dateTime)
    if (isNaN(date.getTime())) {
      return dateTime
    }
    return date.toLocaleString('zh-CN')
  } catch (error) {
    return dateTime
  }
}

onMounted(async () => {
  await loadCategories()
  await loadActivities()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>