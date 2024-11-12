<div class="card">
  {#await info then info}
    <div class="card-header">
      <h3 class="card-title">User info</h3>
    </div>
    <div class="card-body">

      <div class="datagrid">
        <div class="row">
          <div class="col">
            <div class="datagrid-item">
              <div class="datagrid-title">Зарегистрирован</div>
              <div class="datagrid-content">{formatDate(info.user.createdAt)}</div>
            </div>
          </div>
          <div class="col"></div>
        </div>

        <div class="row">
          <div class="col">
            <div class="datagrid-item">
              <div class="datagrid-title">Chat id</div>
              <div class="datagrid-content">{info.user.chatId}</div>
            </div>
          </div>
          <div class="col">
            <div class="datagrid-item">
              <div class="datagrid-title">Username</div>
              <div class="datagrid-content">{info.user.username || '-'}</div>
            </div>
          </div>
        </div>


        <div class="row">
          <div class="col">
            <div class="datagrid-item">
              <div class="datagrid-title">First name</div>
              <div class="datagrid-content">{info.user.firstName || '-'}</div>
            </div>
          </div>
          <div class="col">
            <div class="datagrid-item">
              <div class="datagrid-title">Last Name</div>
              <div class="datagrid-content">{info.user.lastName || '-'}</div>
            </div>
          </div>
        </div>


        <div class="row">
          <div class="col"> <div class="datagrid-item">
            <div class="datagrid-title">Подписка</div>
            <div class="datagrid-content">
              {#if info.user.paidUntil}
                до {formatDate(info.user.paidUntil)}
                {:else}
                нет
                {/if}
              {#if info.user.activePayment}
                <div class="days-left">дейстует ещё {-daysLeft(info.user.paidUntil)} дн. </div>
                {:else}
                {#if info.user.paidUntil != null}
                <div class="days-left">законч. {-daysLeft(info.user.paidUntil)} дн. назад</div>
                  {/if}
                {/if}
            </div>
          </div></div>
          <div class="col">
            <div class="datagrid-item">
              <div class="datagrid-title">Статус подписки</div>
              <div class="datagrid-content">
                {#if info.user.activePayment}
                    <span class="status status-green">Активна</span>

                {:else}
                  {#if info.user.paidUntil != null}
                      <span class="status status-red">Не активна</span>
                  {:else}
                    <span class="status status-orange">Никогда не было</span>
                  {/if}

                {/if}
              </div>
            </div>
          </div>
        </div>



        <div class="row">
          <div class="col"> <div class="datagrid-item">
            <div class="datagrid-title ">Статистика</div>
            <div class="datagrid-content"><span class=" days-left">запросов:</span> {info.stat.totalRequests}</div>
          </div></div>
          <div class="col">
            <div class="datagrid-item">
              <div class="datagrid-title">Прибыль</div>
              <div class="datagrid-content">
                {#if info.stat.totalIncome.length == 0}
                  нет
                  {:else}
                  {#each info.stat.totalIncome as income}
                    <div class="d-flex align-items-center">
                      <div class="pe-1">{income.amount}</div>
                      <div class="currnecy">{income.currency}</div>
                    </div>

                  {/each}
                {/if}

                </div>
            </div>

          </div>
        </div>




      </div>
    </div>

  {:catch error}
    <div class="alert alert-danger" role="alert">
      {error}
    </div>
  {/await}
</div>


<script lang="ts">
    import type {User} from "@/models";
    import api from "@/lib/api";
    import {formatDate} from "@/lib/common";

    const {user}: { user: User } = $props()

  const info = api.getUserInfo(user.chatId)


  function daysLeft(strDate: string): number {
    const date = new Date(strDate)
    const now = new Date()
    const diff = date.getTime() - now.getTime()
    return Math.floor(diff / (1000 * 60 * 60 * 24))
  }

</script>
<style>
  .days-left {
    font-size: 0.8rem;
    opacity: 0.6
  }
  .currnecy {
    font-size: 0.85rem;
    opacity: 0.6
  }
</style>