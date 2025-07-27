function renderBangumiPage(data) {
    renderUserInfo(data)
    renderCollection("anime-collection", data.animeCollectionJson);
    renderCollection("book-collection", data.bookCollectionJson);
    renderCollection("music-collection", data.musicCollectionJson);
    renderCollection("game-collection", data.gameCollectionJson);
    renderCollection("real-collection", data.realCollectionJson);
}

function renderUserInfo(data) {
    const avatarEl = document.getElementById("bangumi-avatar");
    const nicknameEl = document.getElementById("bangumi-nickname");
    const signEl = document.getElementById("bangumi-sign");
    const updateTimeEl = document.getElementById("data-last-update-time");

    const lastUpdateTime = new Date(parseInt(data.lastUpdateTime)).toLocaleString();

    if (avatarEl && nicknameEl && signEl && updateTimeEl) {
        avatarEl.src = data.avatar;
        nicknameEl.textContent = data.nickname;
        signEl.textContent = data.sign;
        updateTimeEl.textContent = `最后更新: ${lastUpdateTime}`;
    }
}

function renderCollection(collectionName, collectionJson) {
    const container = document.querySelector(`#${collectionName} .bangumi-inner`);
    if (!container) return;

    try {
        const collection = JSON.parse(collectionJson);
        if (!collection || collection.length === 0) {
            const collectionContainer = document.getElementById(collectionName);
            if (collectionContainer) {
                collectionContainer.style.display = "none";
            }
            return;
        }

        collection.forEach((item) => {
            console.log(item);

            const card = document.createElement("div");
            card.className = "bangumi-card";
            card.innerHTML = `
                              <a href="https://bangumi.tv/subject/${item.subject_id}" target="_blank" rel="noopener noreferrer">
                                <img src="${item.subject.images.large}" alt="${item.subject.name_cn || item.subject.name}" class="bangumi-cover" />
                              </a>
                              <div class="bangumi-info">
                                <h2 class="bangumi-title">${item.subject.name_cn || item.subject.name}</h2>
                                <div class="bangumi-status">
                                  <div>状态: ${subjectStatus(item.type, item.subject.type)}</div>
                                  <div>进度: ${item.ep_status}/${item.subject.eps}</div>
                                  <div>个人评分: ${item.rate === 0 ? "未评分" : item.rate}</div>
                                  <div>BGM评分: ${item.subject.score}</div>
                                </div>
                              </div>
                            `;
            container.appendChild(card);
        });
    } catch (e) {
        console.error(`Error parsing ${collectionName} JSON:`, e);
    }
}

function subjectStatus(type, subjectType) {
    switch (type) {
        case 1:
            switch (subjectType) {
                case 1:
                    return "想读";
                case 2:
                    return "想看";
                case 3:
                    return "想听";
                case 4:
                    return "想玩";
                case 6:
                default:
                    return "想看";
            }
        case 2:
            switch (subjectType) {
                case 1:
                    return "已读";
                case 2:
                    return "已看";
                case 3:
                    return "已听";
                case 4:
                    return "已玩";
                case 6:
                default:
                    return "已看";
            }
        case 3:
            switch (subjectType) {
                case 1:
                    return "在读";
                case 2:
                    return "在看";
                case 3:
                    return "在听";
                case 4:
                    return "在玩";
                case 6:
                default:
                    return "在看";
            }
        case 4:
            return "搁置";
        default:
            return "抛弃";
    }
}
