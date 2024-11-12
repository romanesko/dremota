<div class="card-header">
  <h3 class="card-title">Пользователи</h3>
</div>
{#if showHeader}
<div class="card-body border-bottom py-3">
  <div class="d-flex">
    <div class="text-secondary">
      Show
      <div class="mx-2 d-inline-block">
        <input type="text" class="form-control form-control-sm" value="8" size="3" aria-label="Invoices count">
      </div>
      entries
    </div>
    <div class="ms-auto text-secondary">
      Search:
      <div class="ms-2 d-inline-block">
        <input type="text" class="form-control form-control-sm" aria-label="Search invoice">
      </div>
    </div>
  </div>
</div>
  {/if}
<div class="table-responsive">
  <table class="table card-table table-vcenter text-nowrap datatable">
    <thead>
    <tr>

      <th class="w-1 text-center">userpic <!-- Download SVG icon from http://tabler-icons.io/i/chevron-up -->

      </th>
      <th class="text-center">Tg user id</th>
      <th>username</th>
      <th>fist name</th>
      <th>last name</th>
      <th class="text-center">$</th>
      <th class="text-center">paid until</th>
      <th class="text-center">last visit
<!--        <img src={chevronUp} alt="chevron-up" style="width: 1rem;"/>-->
      </th>

    </tr>
    </thead>
    <tbody>
    {#each users as user}
      <tr>
        <td class="text-center">
          <span class="avatar me-2" style="background-image: url({getUserPhoto(user)})">
            {#if unread[user.chatId]}
              <div class="badge bg-red"></div>
            {/if}
          </span></td>
        <td class="text-center"><a href="/#/users/{user.chatId}">{user.chatId}</a></td>
        <td class="text-secondary">
          {user.username}
        </td>
        <td class="text-secondary">{user.firstName}</td>
        <td class="text-secondary">
          {user.lastName}
        </td>
        <td class="text-center">
          {#if user.paidUntil}
            {#if user.activePayment}
            <span class="badge bg-success me-1"></span>
              {:else}
            <span class="badge bg-danger me-1"></span>
              {/if}

          {:else}
            <span class="badge bg-secondary me-1"></span>
          {/if}
          </td>
        <td class="text-center text-secondary">
          {formatDate(user.paidUntil)}
        </td>
        <td class="text-center text-secondary">
          {formatDate(user.lastVisit)}
        </td>


      </tr>
    {/each}

    </tbody>
  </table>
</div>
{#if showFooter}
<div class="card-footer d-flex align-items-center">
  <p class="m-0 text-secondary">Showing <span>1</span> to <span>8</span> of <span>16</span> entries</p>
  <ul class="pagination m-0 ms-auto">
    <li class="page-item disabled">
      <a class="page-link" href="#" tabindex="-1" aria-disabled="true">
        <!-- Download SVG icon from http://tabler-icons.io/i/chevron-left -->
        <svg
            xmlns="http://www.w3.org/2000/svg"
            width="24"
            height="24"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
            class="icon">
          <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
          <path d="M15 6l-6 6l6 6"></path>
        </svg>
        prev
      </a>
    </li>
    <li class="page-item"><a class="page-link" href="#">1</a></li>
    <li class="page-item active"><a class="page-link" href="#">2</a></li>
    <li class="page-item"><a class="page-link" href="#">3</a></li>
    <li class="page-item"><a class="page-link" href="#">4</a></li>
    <li class="page-item"><a class="page-link" href="#">5</a></li>
    <li class="page-item">
      <a class="page-link" href="#">
        next <!-- Download SVG icon from http://tabler-icons.io/i/chevron-right -->
        <svg
            xmlns="http://www.w3.org/2000/svg"
            width="24"
            height="24"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
            class="icon">
          <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
          <path d="M9 6l6 6l-6 6"></path>
        </svg>
      </a>
    </li>
  </ul>
</div>
  {/if}


<script lang="ts">
    import type {User} from "@/models";
    import api from "@/lib/api";
    import {alertError, formatDate, getUserPhoto} from "@/lib/common";

    let showHeader = false
  let showFooter = false

  let users = $state([] as User[]);
  let unread : {[key:number]:boolean} = $state({})

  api.usersWithUnreadFeedback().then(a => {
    a.forEach(u => {
      unread[u] = true
    })
  })

  api.getUsers().catch(alertError).then(a => {
    if (a) {
      users = a
    }
  })

</script>