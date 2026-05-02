package com.example.auction.shared.entity;
// Enum định nghĩa các vai trò (role) trong hệ thống đấu giá
// Enum trong Java là một kiểu dữ liệu đặc biệt
// được sử dụng để định nghĩa một tập hợp các hằng số cố định
// (ví dụ: ngày trong tuần, trạng thái đơn hàng).
// Enum giúp mã nguồn rõ ràng, an toàn về kiểu dữ liệu (type-safe) và dễ bảo trì hơn so với việc sử dụng các hằng số static final thông thường.
public enum Role {
    ADMIN,  // có quyền quản lý hệ thống, kh có bidderProfile và sellerProfile chỉ quản lí các member
    MEMBER,  // thanh vien da dang ky, dung 2 profile bidder va seller kia de xac dinh la mua hay ban
    GUEST // khach vang lai, chi xem dc sp, kh co profile, kh dc mua ban, kh can dang nhap
}
