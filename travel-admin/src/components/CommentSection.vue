<template>
  <div class="comment-section">
    <div class="section-header">
      <h3 class="section-title">
        <el-icon><ChatDotRound /></el-icon>
        评论区 ({{ totalCount }})
      </h3>
      <el-button
        v-if="canComment"
        class="light-button"
        @click="showPublishDialog">
        <el-icon><Edit /></el-icon>
        发表评论
      </el-button>
      <el-button v-else type="info" disabled>
        <el-icon><Lock /></el-icon>
        等级不足，无法评论
      </el-button>
    </div>

    <!-- 评论列表 -->
    <div v-loading="loading" class="comment-list">
      <div v-if="commentList.length === 0" class="empty-state">
        <el-empty description="还没有评论，快来抢沙发吧！" />
      </div>

      <div v-for="comment in commentList" :key="comment.id" class="comment-item">
        <!-- 评论头部 -->
        <div class="comment-header">
          <el-avatar 
            :src="comment.avatar" 
            :size="40"
            style="cursor: pointer;"
            @click.stop="viewUserProfile(comment.userId)"
          >{{ comment.username?.charAt(0) }}</el-avatar>
          <div class="user-info">
            <div 
              class="username"
              style="cursor: pointer;"
              @click.stop="viewUserProfile(comment.userId)"
            >
              {{ comment.username }}
              <el-tag v-if="comment.isAuthor" size="small" type="success">作者</el-tag>
              <el-tag 
                v-if="comment.userPoints !== undefined && comment.userPoints !== null" 
                size="small" 
                type="info"
                :style="{ 
                  background: getLevelStyleByPoints(comment.userPoints).gradient 
                    ? `linear-gradient(135deg, ${getLevelStyleByPoints(comment.userPoints).gradient.start}, ${getLevelStyleByPoints(comment.userPoints).gradient.end})`
                    : getLevelStyleByPoints(comment.userPoints).color,
                  color: 'white',
                  border: 'none'
                }"
              >
                {{ getLevelStyleByPoints(comment.userPoints).name }}
              </el-tag>
              <el-tag v-if="comment.isTop" size="small" type="danger">置顶</el-tag>
              <el-tag v-if="comment.isHot" size="small" type="warning">热评</el-tag>
            </div>
            <div class="comment-time">{{ comment.createdTime }}</div>
          </div>
        </div>

        <!-- 评论内容 -->
        <div class="comment-content">
          {{ comment.content }}
        </div>

        <!-- 评论操作栏 -->
        <div class="comment-actions">
          <el-button
            text
            :type="comment.hasLiked ? 'primary' : ''"
            @click="handleLike(comment)">
            <el-icon><CaretTop /></el-icon>
            {{ comment.likeCount || 0 }}
          </el-button>
          <el-button 
            v-if="!hasUserReplied(comment)"
            text 
            @click="handleReply(comment)">
            <el-icon><ChatDotRound /></el-icon>
            回复 ({{ comment.replyCount || 0 }})
          </el-button>
          <span v-else class="replied-hint">已回复</span>
          <el-button
            v-if="canDelete(comment)"
            text
            type="danger"
            @click="handleDelete(comment)">
            <el-icon><Delete /></el-icon>
            删除
          </el-button>
          <el-button text type="warning" @click="handleReport(comment)">
            <el-icon><Warning /></el-icon>
            举报
          </el-button>
        </div>

        <!-- 回复列表 -->
        <div v-if="comment.replies && comment.replies.length > 0" v-show="getDisplayedReplies(comment).length > 0" class="reply-list">
          <div v-for="reply in getDisplayedReplies(comment)" :key="reply.id" class="reply-item">
            <div class="reply-header">
              <el-avatar 
                :src="reply.avatar" 
                :size="28"
                style="cursor: pointer;"
                @click.stop="viewUserProfile(reply.userId)"
              >{{ reply.username?.charAt(0) }}</el-avatar>
              <div class="reply-user-info">
                <span 
                  class="username"
                  style="cursor: pointer;"
                  @click.stop="viewUserProfile(reply.userId)"
                >
                  {{ reply.username }}
                  <el-tag v-if="reply.isAuthor" size="small" type="success">作者</el-tag>
                  <el-tag 
                    v-if="reply.userPoints !== undefined && reply.userPoints !== null" 
                    size="small" 
                    type="info"
                    :style="{ 
                      background: getLevelStyleByPoints(reply.userPoints).gradient 
                        ? `linear-gradient(135deg, ${getLevelStyleByPoints(reply.userPoints).gradient.start}, ${getLevelStyleByPoints(reply.userPoints).gradient.end})`
                        : getLevelStyleByPoints(reply.userPoints).color,
                      color: 'white',
                      border: 'none'
                    }"
                  >
                    {{ getLevelStyleByPoints(reply.userPoints).name }}
                  </el-tag>
                </span>
                <span v-if="reply.replyToUsername" class="reply-to">回复 @{{ reply.replyToUsername }}</span>
                <span class="reply-time">{{ reply.createdTime }}</span>
              </div>
            </div>
            <div class="reply-content">{{ reply.content }}</div>
            <div class="reply-actions">
              <el-button
                text
                size="small"
                :type="reply.hasLiked ? 'primary' : ''"
                @click="handleLike(reply)">
                <el-icon><CaretTop /></el-icon>
                {{ reply.likeCount || 0 }}
              </el-button>
              <el-button 
                v-if="!isReplyToReply(reply)" 
                text 
                size="small" 
                @click="handleReply(reply)">
                <el-icon><ChatDotRound /></el-icon>
                回复
              </el-button>
              <el-button 
                text 
                type="warning" 
                size="small"
                @click="handleReport(reply)">
                <el-icon><Warning /></el-icon>
                举报
              </el-button>
            </div>
          </div>

          <!-- 展开/收起回复按钮 -->
          <div v-if="comment.replies && comment.replies.length > 1" class="view-more-replies">
            <el-button 
              text 
              type="primary" 
              @click="toggleReplies(comment)">
              {{ expandedReplies.has(comment.id) ? '收起回复' : `展开 ${comment.replies.length - 1} 条回复` }}
              <el-icon>
                <ArrowDown v-if="!expandedReplies.has(comment.id)" />
                <ArrowUp v-else />
              </el-icon>
            </el-button>
          </div>
          
          <!-- 加载更多回复按钮（如果还有未加载的回复） -->
          <div v-if="comment.replyCount > (comment.replies?.length || 0)" class="view-more-replies">
            <el-button 
              text 
              type="primary" 
              :loading="loadingReplies.has(comment.id)"
              @click="loadAllReplies(comment)">
              {{ expandedReplies.has(comment.id) ? '收起回复' : `查看全部 ${comment.replyCount} 条回复` }}
              <el-icon>
                <ArrowDown v-if="!expandedReplies.has(comment.id)" />
                <ArrowUp v-else />
              </el-icon>
            </el-button>
          </div>
        </div>
        
        <!-- 如果没有回复但回复数大于0，显示加载按钮 -->
        <div v-else-if="comment.replyCount > 0" class="view-more-replies">
          <el-button 
            text 
            type="primary" 
            :loading="loadingReplies.has(comment.id)"
            @click="loadAllReplies(comment)">
            {{ expandedReplies.has(comment.id) ? '收起回复' : `查看 ${comment.replyCount} 条回复` }}
            <el-icon>
              <ArrowDown v-if="!expandedReplies.has(comment.id)" />
              <ArrowUp v-else />
            </el-icon>
          </el-button>
        </div>
      </div>
    </div>

    <!-- 加载更多评论 -->
    <div v-if="hasMore" class="load-more">
      <el-button 
        class="load-more-btn"
        @click="loadMore" 
        :loading="loading">
        <el-icon v-if="!loading"><ArrowDown /></el-icon>
        加载更多评论
      </el-button>
    </div>

    <!-- 发布评论对话框 -->
    <el-dialog
      v-model="publishDialogVisible"
      :title="isReply ? '回复评论' : '发表评论'"
      width="600px"
      class="publish-dialog">
      <!-- 当前用户信息 -->
      <div v-if="currentUser" class="current-user-info">
        <el-avatar :src="currentUser.avatar" :size="32">
          {{ currentUser.username?.charAt(0) }}
        </el-avatar>
        <div class="user-details">
          <div class="user-name">
            {{ currentUser.username || '用户' }}
            <el-tag 
              v-if="currentUser.userPoints !== undefined && currentUser.userPoints !== null" 
              size="small" 
              type="info"
              :style="{ 
                background: getLevelStyleByPoints(currentUser.userPoints).gradient 
                  ? `linear-gradient(135deg, ${getLevelStyleByPoints(currentUser.userPoints).gradient.start}, ${getLevelStyleByPoints(currentUser.userPoints).gradient.end})`
                  : getLevelStyleByPoints(currentUser.userPoints).color,
                color: 'white',
                border: 'none'
              }"
            >
              {{ getLevelStyleByPoints(currentUser.userPoints).name }}
            </el-tag>
          </div>
        </div>
      </div>
      <el-divider v-if="currentUser" />
      <el-input
        v-model="commentContent"
        type="textarea"
        :rows="6"
        :placeholder="replyPlaceholder"
        maxlength="500"
        show-word-limit />
      <template #footer>
        <el-button type="primary" @click="handlePublish" :loading="publishing">
          {{ isReply ? '发布回复' : '发布评论' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 举报对话框 -->
    <el-dialog
      v-model="reportDialogVisible"
      title="举报评论"
      width="650px"
      class="report-dialog">
      <el-form label-width="100px">
        <el-form-item label="举报原因">
          <el-select v-model="reportReason" placeholder="请选择举报原因">
            <el-option label="垃圾广告" value="SPAM"></el-option>
            <el-option label="色情低俗" value="PORN"></el-option>
            <el-option label="政治敏感" value="POLITICAL"></el-option>
            <el-option label="恶意辱骂" value="ABUSE"></el-option>
            <el-option label="其他" value="OTHER"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="详细说明">
          <el-input
            v-model="reportDescription"
            type="textarea"
            :rows="6"
            placeholder="请详细描述举报原因（可选）"
            maxlength="200"
            show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="handleSubmitReport" :loading="reporting">
          提交举报
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getLevelByPoints } from '@/utils/level';
import {
  ChatDotRound,
  Edit,
  Lock,
  CaretTop,
  Delete,
  Warning,
  ArrowDown,
  ArrowUp
} from '@element-plus/icons-vue';
import request from '@/utils/request';
import { useUserStore } from '@/stores/user';
import { getCurrentUserInfo } from '@/utils/user';

const router = useRouter();

// Props
const props = defineProps({
  contentType: {
    type: String,
    required: true,
    validator: (value) => ['PLAN', 'ATTRACTION', 'CULTURE'].includes(value)
  },
  contentId: {
    type: Number,
    required: true
  }
});

// Emits
const emit = defineEmits(['comment-count-changed']);

const userStore = useUserStore();

// 数据
const loading = ref(false);
const commentList = ref([]);
const totalCount = ref(0);
const currentPage = ref(1);
const pageSize = ref(5);
const canComment = ref(true);

// 回复展开状态和加载状态
const expandedReplies = ref(new Set()); // 存储已展开回复的评论ID
const loadingReplies = ref(new Set()); // 存储正在加载回复的评论ID

// 当前用户积分（从后端获取）
const currentUserPoints = ref(0);

// 当前用户信息
const currentUser = computed(() => {
  const user = userStore.userInfo || userStore.user;
  if (!user) return null;
  // 优先使用从后端获取的积分，其次使用 userPoints，最后使用 points
  const points = currentUserPoints.value || user.userPoints || user.points || 0;
  return {
    id: user.id,
    username: user.nickname || user.name || user.username || '用户', // 优先显示昵称，其次名字，最后账号名
    avatar: user.avatar || '',
    userPoints: points
  };
});

// 对话框
const publishDialogVisible = ref(false);
const reportDialogVisible = ref(false);
const commentContent = ref('');
const publishing = ref(false);
const reporting = ref(false);

// 回复相关
const isReply = ref(false);
const replyTarget = ref(null);

// 举报相关
const reportTarget = ref(null);
const reportReason = ref('');
const reportDescription = ref('');

// 计算属性
const hasMore = computed(() => {
  return commentList.value.length < totalCount.value;
});

// 获取要显示的回复（默认只显示前1条，展开后显示全部）
const getDisplayedReplies = (comment) => {
  if (!comment.replies || comment.replies.length === 0) {
    return [];
  }
  
  // 如果已展开，显示全部
  if (expandedReplies.value.has(comment.id)) {
    return comment.replies;
  }
  
  // 否则只显示前1条
  return comment.replies.slice(0, 1);
};

// 判断是否是回复的回复（禁止三级回复）
const isReplyToReply = (reply) => {
  // 如果reply有parentId，说明它是回复，不能再被回复
  return reply.parentId != null && reply.parentId !== reply.id;
};

const replyPlaceholder = computed(() => {
  if (isReply.value && replyTarget.value) {
    const username = replyTarget.value.username || '用户';
    return `回复 @${username}:`;
  }
  return '请输入评论内容...';
});

// 加载评论列表
const loadComments = async (page = 1) => {
  loading.value = true;
  try {
    const response = await request.get('/user/comment/list', {
      params: {
        contentType: props.contentType,
        contentId: props.contentId,
        page,
        pageSize: pageSize.value
      }
    });

    if (response.code === 200) {
      const comments = response.data.list || [];
      
      // 处理评论数据，确保字段正确
      const processedComments = comments.map(comment => ({
        ...comment,
        // 确保username有值（优先使用nickname）
        username: comment.username || '用户',
        // 确保isAuthor是Boolean类型
        isAuthor: comment.isAuthor === 1 || comment.isAuthor === true || comment.isAuthor === '1',
        // 处理回复数据（初始只显示前3条，如果回复数<=3则默认展开）
        replies: (comment.replies || []).map(reply => ({
          ...reply,
          username: reply.username || '用户',
          isAuthor: reply.isAuthor === 1 || reply.isAuthor === true || reply.isAuthor === '1'
        })),
        // 标记是否已加载所有回复
        allRepliesLoaded: (comment.replies || []).length >= (comment.replyCount || 0)
      }));
      
      // 如果回复数<=1，默认展开；否则默认收起，只显示前1条
      processedComments.forEach(comment => {
        if (comment.replies && comment.replies.length > 0 && comment.replies.length <= 1) {
          expandedReplies.value.add(comment.id);
        }
      });
      
      if (page === 1) {
        commentList.value = processedComments;
      } else {
        commentList.value.push(...processedComments);
      }
      
      // 计算总评论数：顶级评论数 + 所有回复数
      // 后端返回的total是顶级评论数，我们需要加上所有顶级评论的回复数
      const topLevelCount = response.data.total || 0;
      
      // 计算所有顶级评论的回复数总和（包括已加载和未加载的）
      // 由于我们只能知道已加载评论的回复数，这里先计算已加载的
      // 为了更准确，我们需要遍历所有已加载的顶级评论，累加它们的回复数
      let totalReplyCount = 0;
      commentList.value.forEach(comment => {
        // 只统计顶级评论的回复数（parentId为null的评论）
        if (!comment.parentId) {
          totalReplyCount += comment.replyCount || 0;
        }
      });
      
      // 总评论数 = 顶级评论数 + 所有回复数
      // 注意：这里计算的是已加载的顶级评论的回复数总和
      // 如果后端能返回所有顶级评论的回复数总和就更准确
      totalCount.value = topLevelCount + totalReplyCount;
      
      currentPage.value = page;
      // 通知父组件评论数变化
      emit('comment-count-changed', totalCount.value);
    } else {
      ElMessage.error(response.message || '加载评论失败');
    }
  } catch (error) {
    console.error('加载评论失败:', error);
    ElMessage.error('加载评论失败');
  } finally {
    loading.value = false;
  }
};

// 检查评论权限
const checkPermission = async () => {
  try {
    const response = await request.get('/user/comment/check-permission');
    if (response.code === 200) {
      canComment.value = response.data;
    }
  } catch (error) {
    console.error('检查权限失败:', error);
    // 默认允许评论
    canComment.value = true;
  }
};

// 加载更多
const loadMore = () => {
  loadComments(currentPage.value + 1);
};

// 加载所有回复
const loadAllReplies = async (comment) => {
  // 如果已经展开，则收起
  if (expandedReplies.value.has(comment.id)) {
    expandedReplies.value.delete(comment.id);
    return;
  }
  
  // 如果已经加载了所有回复，直接展开
  if (comment.allRepliesLoaded) {
    expandedReplies.value.add(comment.id);
    return;
  }
  
  // 标记正在加载
  loadingReplies.value.add(comment.id);
  
  try {
    const response = await request.get(`/user/comment/replies/${comment.id}`);
    
    if (response.code === 200) {
      const allReplies = response.data || [];
      
      // 处理回复数据
      const processedReplies = allReplies.map(reply => ({
        ...reply,
        username: reply.username || '用户',
        isAuthor: reply.isAuthor === 1 || reply.isAuthor === true || reply.isAuthor === '1'
      }));
      
      // 更新评论的回复列表
      const commentIndex = commentList.value.findIndex(c => c.id === comment.id);
      if (commentIndex !== -1) {
        commentList.value[commentIndex].replies = processedReplies;
        commentList.value[commentIndex].allRepliesLoaded = true;
      }
      
      // 展开回复
      expandedReplies.value.add(comment.id);
    } else {
      ElMessage.error(response.message || '加载回复失败');
    }
  } catch (error) {
    console.error('加载回复失败:', error);
    ElMessage.error('加载回复失败');
  } finally {
    loadingReplies.value.delete(comment.id);
  }
};

// 获取当前用户积分
const loadCurrentUserPoints = async () => {
  const user = userStore.userInfo || userStore.user;
  if (!user || !user.id) return;
  
  try {
    const response = await request.get('/user/points/my', {
      params: { userId: user.id }
    });
    if (response.code === 200 && response.data?.userPoints) {
      currentUserPoints.value = response.data.userPoints.totalPoints || 0;
    }
  } catch (error) {
    console.warn('获取当前用户积分失败:', error);
    // 降级方案：使用store中的积分
    const user = userStore.userInfo || userStore.user;
    currentUserPoints.value = user?.userPoints || user?.points || 0;
  }
};

// 显示发布对话框
const showPublishDialog = async () => {
  // 检查用户是否已经发布过评论
  if (hasUserCommented()) {
    ElMessage.warning('您已经对该内容发布过评论，每个用户只能发布一次评论');
    return;
  }
  
  isReply.value = false;
  replyTarget.value = null;
  commentContent.value = '';
  // 打开对话框时获取最新的用户积分
  await loadCurrentUserPoints();
  publishDialogVisible.value = true;
};

// 检查用户是否已经回复过这条评论
const hasUserReplied = (comment) => {
  const currentUserId = userStore.user?.id || userStore.userInfo?.id;
  if (!currentUserId) {
    return false;
  }
  
  // 如果comment有replies数组，检查回复列表中是否有当前用户的回复
  if (comment.replies && Array.isArray(comment.replies)) {
    return comment.replies.some(reply => reply.userId === currentUserId);
  }
  
  // 如果comment本身是回复，检查是否是当前用户回复的
  if (comment.userId === currentUserId && comment.parentId) {
    return true;
  }
  
  return false;
};

// 检查用户是否已经发布过评论（对同一个内容）
const hasUserCommented = () => {
  const currentUserId = userStore.user?.id || userStore.userInfo?.id;
  if (!currentUserId) {
    return false;
  }
  
  // 检查评论列表中是否有当前用户发布的顶级评论
  return commentList.value.some(comment => 
    comment.userId === currentUserId && 
    !comment.parentId &&  // 顶级评论
    comment.contentType === props.contentType &&
    comment.contentId === props.contentId
  );
};

// 处理回复
const handleReply = async (comment) => {
  if (!canComment.value) {
    ElMessage.warning('您的等级不足，无法回复评论');
    return;
  }
  
  // 禁止三级回复：如果comment是回复（有parentId），则不能再回复
  if (isReplyToReply(comment)) {
    ElMessage.warning('不能对回复进行回复，请直接回复原评论');
    return;
  }
  
  // 检查用户是否已经回复过这条评论（前端预检查，后端会再次验证）
  if (hasUserReplied(comment)) {
    ElMessage.warning('您已经回复过这条评论，每个用户只能回复一次');
    return;
  }
  
  isReply.value = true;
  replyTarget.value = comment;
  commentContent.value = '';
  // 打开对话框时获取最新的用户积分
  await loadCurrentUserPoints();
  publishDialogVisible.value = true;
};

// 切换回复展开/收起状态（仅用于显示已加载的回复）
const toggleReplies = (comment) => {
  if (expandedReplies.value.has(comment.id)) {
    expandedReplies.value.delete(comment.id);
  } else {
    expandedReplies.value.add(comment.id);
  }
};

// 检查评论权限和限制
const checkCommentPermission = async () => {
  try {
    const userInfo = getCurrentUserInfo();
    if (!userInfo || !userInfo.id) {
      return true; // 如果没有用户信息，允许继续（降级处理）
    }
    
    // 检查是否有评论权限
    const permissionRes = await request.get('/user/points/check-permission', {
      params: {
        userId: userInfo.id,
        permissionType: 'COMMENT'
      }
    });
    
    if (permissionRes.code !== 200 || !permissionRes.data?.hasPermission) {
      ElMessage.warning('您当前等级不支持发布评论，请先完成任务升级');
      return false;
    }
    
    // 检查今日评论次数
    const pointsRes = await request.get('/user/points/my', {
      params: { userId: userInfo.id }
    });
    
    if (pointsRes.code === 200 && pointsRes.data) {
      const todayComments = pointsRes.data.todayComments || 0;
      const dailyCommentLimit = pointsRes.data.dailyCommentLimit || 0;
      
      if (dailyCommentLimit > 0 && todayComments >= dailyCommentLimit) {
        ElMessage.warning(`今日评论已达上限（${dailyCommentLimit}条），请明天再试`);
        return false;
      }
    }
    
    return true;
  } catch (error) {
    console.error('检查评论权限失败:', error);
    // 如果检查失败，允许继续发布（降级处理）
    return true;
  }
};

// 发布评论/回复
const handlePublish = async () => {
  if (!commentContent.value.trim()) {
    ElMessage.warning('请输入内容');
    return;
  }

  // 检查评论权限和限制
  const canComment = await checkCommentPermission();
  if (!canComment) {
    return;
  }

  publishing.value = true;
  try {
    let response;
    if (isReply.value && replyTarget.value) {
      // 再次检查用户是否已经回复过（防止并发情况）
      const parentId = replyTarget.value.parentId || replyTarget.value.id;
      const parentComment = commentList.value.find(c => c.id === parentId);
      if (parentComment && hasUserReplied(parentComment)) {
        ElMessage.warning('您已经回复过这条评论，每个用户只能回复一次');
        publishing.value = false;
        return;
      }
      
      // 发布回复
      response = await request.post('/user/comment/reply', null, {
        params: {
          parentId: parentId,
          replyToUserId: replyTarget.value.userId,
          content: commentContent.value
        }
      });
    } else {
      // 再次检查用户是否已经发布过评论（防止并发情况）
      if (hasUserCommented()) {
        ElMessage.warning('您已经对该内容发布过评论，每个用户只能发布一次评论');
        publishing.value = false;
        return;
      }
      
      // 发布评论
      response = await request.post('/user/comment/publish', {
        contentType: props.contentType,
        contentId: props.contentId,
        content: commentContent.value
      });
    }

    if (response.code === 200) {
      ElMessage.success(isReply.value ? '回复成功' : '发布成功');
      publishDialogVisible.value = false;
      commentContent.value = '';
      
      if (isReply.value && replyTarget.value) {
        // 如果是回复，需要更新对应评论的回复列表
        const parentId = replyTarget.value.parentId || replyTarget.value.id;
        const commentIndex = commentList.value.findIndex(c => c.id === parentId);
        
        if (commentIndex !== -1) {
          // 如果已经加载了所有回复，重新加载
          if (commentList.value[commentIndex].allRepliesLoaded) {
            await loadAllReplies(commentList.value[commentIndex]);
          } else {
            // 否则重新加载评论列表
            await loadComments(1);
          }
        } else {
          // 重新加载评论列表
          await loadComments(1);
        }
      } else {
        // 发布新评论，重新加载评论列表
        await loadComments(1);
      }
    } else {
      ElMessage.error(response.message || '发布失败');
    }
  } catch (error) {
    console.error('发布失败:', error);
    ElMessage.error('发布失败');
  } finally {
    publishing.value = false;
  }
};

// 点赞/取消点赞
const handleLike = async (comment) => {
  try {
    const response = await request.post(`/user/comment/like/${comment.id}`);
    if (response.code === 200) {
      comment.hasLiked = response.data;
      comment.likeCount += response.data ? 1 : -1;
    } else {
      ElMessage.error(response.message || '操作失败');
    }
  } catch (error) {
    console.error('点赞失败:', error);
    ElMessage.error('操作失败');
  }
};

// 删除评论
const handleDelete = async (comment) => {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗？', '确认删除', {
      type: 'warning'
    });

    const response = await request.delete(`/user/comment/delete/${comment.id}`);
    if (response.code === 200) {
      ElMessage.success('删除成功');
      await loadComments(1);
    } else {
      ElMessage.error(response.message || '删除失败');
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error);
      ElMessage.error('删除失败');
    }
  }
};

// 举报评论
const handleReport = (comment) => {
  reportTarget.value = comment;
  reportReason.value = '';
  reportDescription.value = '';
  reportDialogVisible.value = true;
};

// 提交举报
const handleSubmitReport = async () => {
  if (!reportReason.value) {
    ElMessage.warning('请选择举报原因');
    return;
  }

  reporting.value = true;
  try {
    const response = await request.post('/user/report/submit', {
      targetType: 'COMMENT',
      targetId: reportTarget.value.id,
      reason: reportReason.value,
      description: reportDescription.value
    });

    if (response.code === 200) {
      ElMessage.success('举报成功，我们会尽快处理');
      reportDialogVisible.value = false;
    } else {
      ElMessage.error(response.message || '举报失败');
    }
  } catch (error) {
    console.error('举报失败:', error);
    ElMessage.error('举报失败');
  } finally {
    reporting.value = false;
  }
};

// 判断是否可以删除
const canDelete = (comment) => {
  // 只能删除自己的评论，或者管理员可以删除所有评论
  const currentUserId = userStore.user?.id;
  const isAdmin = userStore.user?.role === 'admin';
  return currentUserId === comment.userId || isAdmin;
};

// 根据积分获取等级样式信息（统一使用升级指南的计算方式）
const getLevelStyleByPoints = (points) => {
  if (points === undefined || points === null || points < 0) {
    return { name: '青铜旅行者', color: '#8B7355', gradient: { start: '#8B7355', end: '#6B5B4F' } };
  }
  return getLevelByPoints(points);
};

// 查看用户主页
const viewUserProfile = (userId) => {
  if (!userId) {
    ElMessage.warning('用户ID不存在');
    return;
  }
  router.push(`/home/user/profile/${userId}`);
};

// 初始化
onMounted(() => {
  loadComments();
  checkPermission();
  // 初始化时获取当前用户积分
  loadCurrentUserPoints();
});
</script>

<style scoped lang="scss">
.comment-section {
  margin-top: 32px;
  padding: 24px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    padding-bottom: 16px;
    border-bottom: 2px solid #f0f0f0;

    .section-title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 18px;
      font-weight: 600;
      color: #333;
      margin: 0;

      .el-icon {
        font-size: 20px;
        color: #409eff;
      }
    }

      .light-button {
        background: #fff;
        border: 1px solid #e0e0e0;
        color: #4a4a4a;
        border-radius: 8px;
        padding: 0 18px;
        height: 38px;
        display: inline-flex;
        align-items: center;
        gap: 6px;
        transition: all 0.2s ease;

        .el-icon {
          font-size: 16px;
        }

        &:hover {
          background: #f5f7fa;
          border-color: #cdd4de;
          color: #1f2d3d;
        }
      }
  }

  .comment-list {
    .empty-state {
      padding: 40px 0;
    }

    .comment-item {
      padding: 20px;
      margin-bottom: 16px;
      background: #fafafa;
      border-radius: 8px;
      transition: all 0.3s;

      &:hover {
        background: #f5f5f5;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
      }

      .comment-header {
        display: flex;
        gap: 12px;
        margin-bottom: 12px;

        .user-info {
          flex: 1;

          .username {
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 14px;
            font-weight: 600;
            color: #333;
            margin-bottom: 4px;
          }

          .comment-time {
            font-size: 12px;
            color: #999;
          }
        }
      }

      .comment-content {
        font-size: 14px;
        line-height: 1.8;
        color: #666;
        margin-bottom: 12px;
        padding-left: 52px;
      }

      .comment-actions {
        display: flex;
        gap: 16px;
        padding-left: 52px;
        align-items: center;

        :deep(.el-button) {
          display: inline-flex;
          align-items: center;
          gap: 4px;
        }
        
        .replied-hint {
          font-size: 14px;
          color: #909399;
          padding: 0 8px;
        }
      }

      .reply-list {
        margin-top: 16px;
        padding-left: 52px;

        .reply-item {
          padding: 12px;
          background: #fff;
          border-radius: 6px;
          margin-bottom: 8px;

          .reply-header {
            display: flex;
            gap: 8px;
            margin-bottom: 8px;

            .reply-user-info {
              display: flex;
              align-items: center;
              flex-wrap: wrap;
              gap: 8px;
              font-size: 12px;

              .username {
                font-weight: 600;
                color: #333;
              }

              .reply-to {
                color: #409eff;
              }

              .reply-time {
                color: #999;
              }
            }
          }

          .reply-content {
            font-size: 13px;
            line-height: 1.6;
            color: #666;
            padding-left: 36px;
            margin-bottom: 8px;
          }

          .reply-actions {
            display: flex;
            gap: 12px;
            padding-left: 36px;
            align-items: center;

            :deep(.el-button) {
              display: inline-flex;
              align-items: center;
              gap: 4px;
            }
          }
        }

        .view-more-replies {
          padding: 8px 12px;
          text-align: center;
          
          :deep(.el-button) {
            color: #333333 !important;
            
            &:hover {
              color: #1a1a1a !important;
            }
          }
        }
      }
    }
  }

  .load-more {
    padding: 20px 0;
    text-align: center;
    min-height: 60px; // 固定最小高度，防止抖动
    display: flex;
    align-items: center;
    justify-content: center;
    
    .load-more-btn {
      background: #ffffff !important;
      border: 1px solid #e0e0e0 !important;
      color: #333333 !important;
      border-radius: 8px !important;
      padding: 10px 24px !important;
      height: 40px !important;
      font-size: 14px !important;
      transition: all 0.2s ease !important;
      min-width: 140px; // 固定最小宽度，防止抖动
      
      &:hover {
        background: #f5f5f5 !important;
        border-color: #d0d0d0 !important;
        color: #1a1a1a !important;
      }
      
      &:active {
        background: #eeeeee !important;
        border-color: #c0c0c0 !important;
      }
      
      .el-icon {
        font-size: 16px;
        margin-right: 4px;
      }
      
      &.is-loading {
        background: #ffffff !important;
        border-color: #e0e0e0 !important;
        color: #999999 !important;
      }
    }
  }
}

.publish-dialog {
  :deep(.el-dialog__header) {
    border-bottom: 1px solid #f0f0f0;
  }

  :deep(.el-dialog__footer) {
    border-top: 1px solid #f0f0f0;
  }
}

// 发布对话框中的当前用户信息
.publish-dialog {
  .current-user-info {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px;
    background: #f5f7fa;
    border-radius: 8px;
    margin-bottom: 16px;

    .user-details {
      flex: 1;

      .user-name {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 14px;
        font-weight: 600;
        color: #333;
      }
    }
  }
}
</style>

<style lang="scss">
// 发布评论对话框全局样式（非 scoped，因为 dialog 渲染在 body 下）
.el-overlay .publish-dialog {
  .el-button {
    border-radius: 4px !important;
    
    // 取消按钮样式
    &.el-button--default {
      background: #ffffff !important;
      border: 1px solid #e0e0e0 !important;
      color: #666666 !important;
      
      &:hover {
        background: #fafafa !important;
        border-color: #d0d0d0 !important;
        color: #333333 !important;
      }
    }
    
    // 发布按钮样式（改为白色系）
    &.el-button--primary {
      background: #ffffff !important;
      border: 1px solid #e0e0e0 !important;
      color: #333333 !important;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08) !important;
      
      &:hover {
        background: #f5f5f5 !important;
        border-color: #d0d0d0 !important;
        color: #1a1a1a !important;
        box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1) !important;
      }
      
      &:active {
        background: #eeeeee !important;
        border-color: #c0c0c0 !important;
        box-shadow: 0 1px 2px rgba(0, 0, 0, 0.08) !important;
      }
      
      &.is-loading {
        background: #ffffff !important;
        border-color: #e0e0e0 !important;
        color: #999999 !important;
      }
    }
  }
  
  // 输入框样式：去掉hover和focus的transform效果，只保留阴影
  .el-textarea {
    // 去掉外层wrapper的默认focus样式
    &.is-focus {
      .el-textarea__inner {
        border-color: #e0e0e0 !important;
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08) !important;
        outline: none !important;
      }
    }
    
    .el-textarea__inner {
      background: #ffffff !important;
      border: 1px solid #e0e0e0 !important;
      border-radius: 8px !important;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08) !important;
      transition: box-shadow 0.2s ease, border-color 0.2s ease !important;
      outline: none !important;
      
      &:hover {
        border-color: #d0d0d0 !important;
        box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1) !important;
      }
      
      &:focus {
        border-color: #e0e0e0 !important;
        box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1) !important;
        outline: none !important;
      }
    }
    
    // 确保外层wrapper也没有蓝色边框
    .el-textarea__wrapper {
      outline: none !important;
      box-shadow: none !important;
      
      &:focus,
      &:focus-within {
        outline: none !important;
        box-shadow: none !important;
      }
    }
  }
}

// 举报对话框全局样式（非 scoped，因为 dialog 渲染在 body 下）
.el-overlay .report-dialog {
  .el-dialog {
    background: #ffffff !important;
    border-radius: 8px !important;
  }

  .el-dialog__header {
    background: #ffffff !important;
    border-bottom: 1px solid #f5f5f5 !important;
    padding: 24px 32px !important;
    margin: 0 !important;
    
    .el-dialog__title {
      color: #333333 !important;
      font-weight: 500 !important;
      font-size: 18px !important; // 增大标题字体
    }
    
    .el-dialog__headerbtn {
      .el-dialog__close {
        color: #909399 !important;
        font-size: 20px !important; // 增大关闭按钮
        
        &:hover {
          color: #606266 !important;
        }
      }
    }
  }

  .el-dialog__body {
    background: #ffffff !important;
    padding: 32px !important; // 增大内边距
  }

  .el-dialog__footer {
    background: #ffffff !important;
    border-top: 1px solid #f5f5f5 !important;
    padding: 20px 32px !important; // 增大内边距
    margin: 0 !important;
  }

  .el-form-item {
    margin-bottom: 24px !important; // 增大表单项间距
    display: flex !important;
    align-items: center !important; // 垂直居中对齐

  .el-form-item__label {
    color: #666666 !important;
      font-size: 15px !important; // 增大标签字体
      font-weight: 500 !important;
      line-height: 44px !important; // 与输入框高度一致，实现垂直居中
      padding-bottom: 0 !important;
    }
    
    .el-form-item__content {
      display: flex !important;
      align-items: center !important; // 内容区域也垂直居中
    }
  }

  // 输入框样式：初始状态就有阴影，去掉hover和focus的transform效果，去除蓝色边框
  .el-select {
    .el-select__wrapper {
    background: #ffffff !important;
      border: 1px solid #e0e0e0 !important;
      border-radius: 8px !important;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08) !important; // 初始状态就有阴影
      transition: box-shadow 0.2s ease, border-color 0.2s ease !important;
      outline: none !important;
      min-height: 44px !important; // 增大输入框高度
      padding: 10px 14px !important; // 增大内边距
      
      .el-select__placeholder {
        font-size: 15px !important; // 增大占位符字体
      }
      
      .el-select__selected-item {
        font-size: 15px !important; // 增大选中项字体
      }
      
      &:hover {
        border-color: #d0d0d0 !important;
        box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1) !important;
      }
      
      &:focus,
      &:focus-within {
    border-color: #e0e0e0 !important;
        box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1) !important;
        outline: none !important;
      }
    }
    
    // 去掉外层wrapper的默认focus样式
    &.is-focus {
      .el-select__wrapper {
        border-color: #e0e0e0 !important;
        box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1) !important;
        outline: none !important;
      }
    }
  }
  
  .el-textarea {
    .el-textarea__inner {
      background: #ffffff !important;
      border: 1px solid #e0e0e0 !important;
      border-radius: 8px !important;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08) !important; // 初始状态就有阴影
      transition: box-shadow 0.2s ease, border-color 0.2s ease !important;
      outline: none !important;
      font-size: 15px !important; // 增大字体
      line-height: 1.6 !important;
      padding: 12px 14px !important; // 增大内边距
      
      &::placeholder {
        font-size: 15px !important; // 增大占位符字体
      }
    
    &:hover {
        border-color: #d0d0d0 !important;
        box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1) !important;
      }
      
      &:focus {
        border-color: #e0e0e0 !important;
        box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1) !important;
        outline: none !important;
      }
    }
    
    // 去掉外层wrapper的默认focus样式
    &.is-focus {
      .el-textarea__inner {
        border-color: #e0e0e0 !important;
        box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1) !important;
        outline: none !important;
      }
    }
    
    // 确保外层wrapper也没有蓝色边框
    .el-textarea__wrapper {
      outline: none !important;
      box-shadow: none !important;
      
      &:focus,
      &:focus-within {
        outline: none !important;
        box-shadow: none !important;
      }
    }
    
    // 增大字数统计字体
    .el-input__count {
      font-size: 13px !important;
    }
  }

  .el-button {
    border-radius: 6px !important;
    font-size: 15px !important; // 增大按钮字体
    padding: 12px 28px !important; // 增大按钮内边距
    height: auto !important; // 自动高度
    min-height: 44px !important; // 最小高度
    
    // 取消按钮样式
    &.el-button--default {
      background: #ffffff !important;
      border: 1px solid #e0e0e0 !important;
      color: #666666 !important;
      
      &:hover {
        background: #fafafa !important;
        border-color: #d0d0d0 !important;
        color: #333333 !important;
      }
    }
    
    // 提交按钮样式（改为白色系）
    &.el-button--primary {
      background: #ffffff !important;
      border: 1px solid #e0e0e0 !important;
      color: #333333 !important;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08) !important;
      font-weight: 500 !important; // 加粗字体
      
      &:hover {
        background: #f5f5f5 !important;
        border-color: #d0d0d0 !important;
        color: #1a1a1a !important;
        box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1) !important;
      }
      
      &:active {
        background: #eeeeee !important;
        border-color: #c0c0c0 !important;
        box-shadow: 0 1px 2px rgba(0, 0, 0, 0.08) !important;
      }
      
      &.is-loading {
        background: #ffffff !important;
        border-color: #e0e0e0 !important;
        color: #999999 !important;
      }
    }
  }
}
</style>







