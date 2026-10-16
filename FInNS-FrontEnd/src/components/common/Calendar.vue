<template>
  <div ref="el" class="spending-calendar"></div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue';
import { Calendar } from '@fullcalendar/core';
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin from '@fullcalendar/interaction';
import api from '@/api';
import { formatDate, formatWon } from '@/util/format';

const props = defineProps({
  userNo: { type: [Number, String], required: true },
});
// 날짜를 고르면 'YYYY-MM-DD' 를 알린다
const emit = defineEmits(['select']);

const el = ref(null);
let calendar;

const loadDailyAmounts = async () => {
  try {
    const { data } = await api.get(`/users/${props.userNo}/amountByDate`);
    data.forEach((item) => calendar.addEvent({ title: formatWon(item.amount), start: item.transactionDate }));
  } catch (error) {
    console.error('날짜별 소비 금액을 불러오지 못했습니다.', error);
  }
};

onMounted(() => {
  calendar = new Calendar(el.value, {
    plugins: [dayGridPlugin, interactionPlugin],
    locale: 'ko',
    selectable: true,
    headerToolbar: { left: 'prev', center: 'title', right: 'next' },
    showNonCurrentDates: false,
    fixedWeekCount: false,
    height: 'auto',
    eventTextColor: '#0A6DA6',
    eventBackgroundColor: 'transparent',
    eventBorderColor: 'transparent',
    // '1일' → '1'
    dayCellContent: (info) => info.dayNumberText.replace('일', ''),
    select: (info) => emit('select', info.startStr),
    eventClick: (info) => calendar.select(info.event.startStr),
  });
  calendar.render();
  loadDailyAmounts();
  emit('select', formatDate(new Date()));
});

onBeforeUnmount(() => calendar?.destroy());
</script>

<style scoped>
.spending-calendar :deep(.fc-event-title) {
  font-size: 0.95rem;
  font-weight: 700;
  text-align: center;
}

.spending-calendar :deep(.fc-toolbar-title) {
  font-size: 1.5rem;
}

.spending-calendar :deep(.fc-daygrid-day-frame) {
  min-height: 64px;
  cursor: pointer;
}

@media (max-width: 575.98px) {
  .spending-calendar :deep(.fc-event-title) {
    font-size: 0.7rem;
  }
}
</style>
