<template>
  <div class="paper-list">
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" class="search-form">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="关键词">
              <el-input
                v-model="searchForm.keyword"
                placeholder="请输入关键词"
                clearable
                @keyup.enter="handleSearch"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="作者">
              <el-input
                v-model="searchForm.author"
                placeholder="请输入作者"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="机构">
              <el-input
                v-model="searchForm.institution"
                placeholder="请输入机构"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="排序">
              <el-select v-model="searchForm.sortBy" placeholder="排序方式" style="width: 100%">
                <el-option label="默认排序" value="" />
                <el-option label="被引次数" value="totalCitations" />
                <el-option label="发表年份" value="publicationYear" />
                <el-option label="标题" value="title" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="年份范围">
              <el-date-picker
                v-model="yearRange"
                type="yearrange"
                range-separator="至"
                start-placeholder="开始年份"
                end-placeholder="结束年份"
                value-format="YYYY"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序方式">
              <el-radio-group v-model="searchForm.sortOrder">
                <el-radio-button value="desc">降序</el-radio-button>
                <el-radio-button value="asc">升序</el-radio-button>
              </el-radio-group>
              <span class="search-buttons">
                <el-button type="primary" @click="handleSearch">
                  <el-icon><Search /></el-icon>
                  搜索
                </el-button>
                <el-button @click="handleReset">重置</el-button>
              </span>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="table-header">
          <div class="header-left">
            <span>论文列表</span>
            <el-tag type="info">共 {{ paperStore.total }} 条</el-tag>
          </div>
          <div class="header-right">
            <el-upload
              :show-file-list="false"
              :before-upload="handleBeforeUpload"
              accept=".json,.csv"
              style="margin-right: 10px"
            >
              <el-button type="success">
                <el-icon><Upload /></el-icon>
                批量导入
              </el-button>
            </el-upload>
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              新增论文
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        v-loading="paperStore.loading"
        :data="paperStore.papers"
        stripe
        style="width: 100%"
      >
        <el-table-column type="index" label="序号" width="70" align="center" />
        <el-table-column prop="title" label="标题" min-width="280" show-overflow-tooltip>
          <template #default="{ row }">
            <el-link type="primary" @click="goToDetail(row.id)">
              {{ row.title }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column prop="authors" label="作者" width="200" show-overflow-tooltip>
          <template #default="{ row }">
            <span v-for="(author, index) in row.authors" :key="author.id">
              {{ author.name }}
              <span v-if="index < row.authors.length - 1">, </span>
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="journalName" label="期刊" width="160" show-overflow-tooltip />
        <el-table-column prop="publicationYear" label="年份" width="100" align="center" sortable />
        <el-table-column prop="totalCitations" label="被引次数" width="110" align="center" sortable />
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="goToDetail(row.id)">详情</el-button>
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="paperStore.pageNum"
        v-model:page-size="paperStore.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="paperStore.total"
        layout="total, sizes, prev, pager, next, jumper"
        class="pagination"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      @close="handleDialogClose"
    >
      <el-form
        ref="paperFormRef"
        :model="paperForm"
        :rules="paperFormRules"
        label-width="100px"
      >
        <el-form-item label="标题" prop="title">
          <el-input v-model="paperForm.title" placeholder="请输入论文标题" />
        </el-form-item>
        <el-form-item label="作者" prop="authors">
          <el-input
            v-model="paperForm.authorsInput"
            placeholder="多个作者用逗号分隔"
            type="textarea"
            :rows="2"
          />
        </el-form-item>
        <el-form-item label="期刊" prop="journal">
          <el-input v-model="paperForm.journalName" placeholder="请输入期刊名称" />
        </el-form-item>
        <el-form-item label="年份" prop="year">
          <el-input-number v-model="paperForm.year" :min="1900" :max="2100" />
        </el-form-item>
        <el-form-item label="关键词">
          <el-input
            v-model="paperForm.keywordsInput"
            placeholder="多个关键词用逗号分隔"
          />
        </el-form-item>
        <el-form-item label="DOI">
          <el-input v-model="paperForm.doi" placeholder="请输入DOI" />
        </el-form-item>
        <el-form-item label="卷/期">
          <el-input v-model="paperForm.volume" placeholder="卷" style="width: 45%" />
          <span style="margin: 0 10px">/</span>
          <el-input v-model="paperForm.issue" placeholder="期" style="width: 45%" />
        </el-form-item>
        <el-form-item label="页码">
          <el-input v-model="paperForm.pages" placeholder="请输入页码" />
        </el-form-item>
        <el-form-item label="摘要">
          <el-input
            v-model="paperForm.abstract"
            type="textarea"
            :rows="4"
            placeholder="请输入摘要"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="importDialogVisible" title="批量导入" width="500px">
      <div class="import-content">
        <el-upload
          drag
          :auto-upload="false"
          :show-file-list="true"
          :file-list="importFileList"
          :on-change="handleImportFileChange"
          :on-remove="handleImportFileRemove"
          accept=".json,.csv"
          multiple
        >
          <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
          <div class="el-upload__text">
            将文件拖到此处，或<em>点击上传</em>
          </div>
          <template #tip>
            <div class="el-upload__tip">
              支持 JSON/CSV 格式文件
            </div>
          </template>
        </el-upload>
        <div v-if="importResult" class="import-result">
          <el-alert
            :title="`导入完成：成功 ${importResult.successCount} 条，失败 ${importResult.failCount} 条`"
            :type="importResult.failCount > 0 ? 'warning' : 'success'"
            show-icon
            :closable="false"
          />
        </div>
      </div>
      <template #footer>
        <el-button @click="importDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleImport" :loading="importLoading">
          开始导入
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, type FormInstance, type UploadFile, type UploadFiles } from 'element-plus'
import { Search, Upload, Plus, UploadFilled } from '@element-plus/icons-vue'
import { usePaperStore } from '@/stores/paper'
import type { Paper, PaperQueryParams, ImportResult } from '@/types'

const router = useRouter()
const paperStore = usePaperStore()

const searchForm = reactive({
  keyword: '',
  author: '',
  institution: '',
  sortBy: '',
  sortOrder: 'desc' as 'asc' | 'desc'
})

const yearRange = ref<string[]>([])

const dialogVisible = ref(false)
const dialogTitle = ref('')
const submitLoading = ref(false)
const isEdit = ref(false)
const paperFormRef = ref<FormInstance>()

const paperForm = reactive({
  id: 0,
  title: '',
  authorsInput: '',
  journalName: '',
  year: new Date().getFullYear(),
  keywordsInput: '',
  doi: '',
  volume: '',
  issue: '',
  pages: '',
  abstract: ''
})

const paperFormRules = {
  title: [{ required: true, message: '请输入论文标题', trigger: 'blur' }],
  authorsInput: [{ required: true, message: '请输入作者', trigger: 'blur' }],
  journalName: [{ required: true, message: '请输入期刊名称', trigger: 'blur' }],
  year: [{ required: true, message: '请选择年份', trigger: 'change' }]
}

const importDialogVisible = ref(false)
const importLoading = ref(false)
const importFileList = ref<UploadFile[]>([])
const importResult = ref<ImportResult | null>(null)

function handleSearch() {
  const params: PaperQueryParams = {
    keyword: searchForm.keyword || undefined,
    sortBy: searchForm.sortBy || undefined,
    sortOrder: searchForm.sortOrder,
    pageNum: 1,
    pageSize: paperStore.pageSize
  }
  if (yearRange.value && yearRange.value.length === 2) {
    params.yearStart = parseInt(yearRange.value[0])
    params.yearEnd = parseInt(yearRange.value[1])
  }
  paperStore.fetchPapers(params)
}

function handleReset() {
  searchForm.keyword = ''
  searchForm.author = ''
  searchForm.institution = ''
  searchForm.sortBy = ''
  searchForm.sortOrder = 'desc'
  yearRange.value = []
  paperStore.setPage(1)
  paperStore.fetchPapers()
}

function goToDetail(id: number) {
  router.push(`/paper/${id}`)
}

function handleSizeChange(size: number) {
  paperStore.setPageSize(size)
  handleSearch()
}

function handleCurrentChange(page: number) {
  paperStore.setPage(page)
  handleSearch()
}

function handleAdd() {
  isEdit.value = false
  dialogTitle.value = '新增论文'
  resetPaperForm()
  dialogVisible.value = true
}

function handleEdit(row: Paper) {
  isEdit.value = true
  dialogTitle.value = '编辑论文'
  paperForm.id = row.id
  paperForm.title = row.title
  paperForm.authorsInput = row.authors?.map(a => a.name).join(', ') || ''
  paperForm.journalName = row.journalName || ''
  paperForm.year = row.publicationYear || new Date().getFullYear()
  paperForm.keywordsInput = row.keywords || ''
  paperForm.doi = row.doi || ''
  paperForm.volume = row.volume || ''
  paperForm.issue = row.issue || ''
  paperForm.pages = row.pages || ''
  paperForm.abstract = row.abstractText || ''
  dialogVisible.value = true
}

async function handleDelete(row: Paper) {
  try {
    await ElMessageBox.confirm(`确定要删除论文「${row.title}」吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await paperStore.removePaper(row.id)
    ElMessage.success('删除成功')
    handleSearch()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Delete error:', error)
    }
  }
}

function resetPaperForm() {
  paperForm.id = 0
  paperForm.title = ''
  paperForm.authorsInput = ''
  paperForm.journalName = ''
  paperForm.year = new Date().getFullYear()
  paperForm.keywordsInput = ''
  paperForm.doi = ''
  paperForm.volume = ''
  paperForm.issue = ''
  paperForm.pages = ''
  paperForm.abstract = ''
  paperFormRef.value?.resetFields()
}

function handleDialogClose() {
  resetPaperForm()
}

async function handleSubmit() {
  if (!paperFormRef.value) return
  try {
    await paperFormRef.value.validate()
    submitLoading.value = true

    const paperData: Partial<Paper> = {
      title: paperForm.title,
      publicationYear: paperForm.year,
      doi: paperForm.doi || undefined,
      volume: paperForm.volume || undefined,
      issue: paperForm.issue || undefined,
      pages: paperForm.pages || undefined,
      abstractText: paperForm.abstract,
      keywords: paperForm.keywordsInput
        ? paperForm.keywordsInput.split(',').map(k => k.trim()).filter(k => k).join(', ')
        : '',
      authors: paperForm.authorsInput
        ? paperForm.authorsInput.split(',').map((name, index) => ({
            id: index + 1,
            name: name.trim()
          }))
        : [],
      journalName: paperForm.journalName
    }

    if (isEdit.value) {
      await paperStore.editPaper(paperForm.id, paperData)
      ElMessage.success('编辑成功')
    } else {
      await paperStore.addPaper(paperData)
      ElMessage.success('新增成功')
    }

    dialogVisible.value = false
    handleSearch()
  } catch (error) {
    if (error !== false) {
      console.error('Submit error:', error)
    }
  } finally {
    submitLoading.value = false
  }
}

function handleBeforeUpload(file: File) {
  const isJsonOrCsv = file.name.endsWith('.json') || file.name.endsWith('.csv')
  if (!isJsonOrCsv) {
    ElMessage.error('只支持 JSON/CSV 格式文件！')
    return false
  }
  importFileList.value = [{
    name: file.name,
    uid: Date.now().toString(),
    raw: file,
    status: 'ready'
  } as unknown as UploadFile]
  importDialogVisible.value = true
  importResult.value = null
  return false
}

function handleImportFileChange(file: UploadFile, files: UploadFiles) {
  importFileList.value = files
}

function handleImportFileRemove() {
  importFileList.value = []
}

async function handleImport() {
  if (importFileList.value.length === 0) {
    ElMessage.warning('请选择要导入的文件')
    return
  }

  importLoading.value = true
  try {
    const formData = new FormData()
    importFileList.value.forEach(file => {
      if (file.raw) {
        formData.append('files', file.raw)
      }
    })

    const result = await paperStore.importPapers(formData)
    importResult.value = result
    ElMessage.success('导入完成')
    handleSearch()
  } catch (error) {
    console.error('Import error:', error)
  } finally {
    importLoading.value = false
  }
}

onMounted(() => {
  paperStore.fetchPapers()
})
</script>

<style scoped>
.paper-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.search-card {
  border-radius: 8px;
}

.search-form {
  margin: 0;
}

.search-buttons {
  margin-left: 20px;
}

.table-card {
  border-radius: 8px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-right {
  display: flex;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.import-content {
  padding: 10px 0;
}

.import-result {
  margin-top: 20px;
}
</style>
